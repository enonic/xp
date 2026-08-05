package com.enonic.xp.repo.impl.version.search;

import java.time.Instant;
import java.util.List;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.storage.spi.VersionQuery;

/**
 * Translates the enumerated {@link NodeVersionQuery} shapes of the storage-source callers
 * (nodb/BUILD-PHASE-3.5.md Gate 0 inventory: GetNodeVersionsCommand, RepoDumper,
 * VersionTableVacuumCommand, SegmentVacuumCommand, IsBlobUsedByVersionCommand) into the
 * bounded SPI {@link VersionQuery}. Any construct outside that inventory fails loudly with
 * {@link IllegalArgumentException} naming it — a predicate must never be silently dropped.
 * One deliberate widening: a bare {@code timestamp DESC} ordering maps to
 * {@link VersionQuery.Order#TS_DESC_ID_ASC}, the version-id tiebreaker implied
 * (elasticsearch leaves equal-timestamp order undefined; the SPI order pins it).
 */
public class SpiVersionQueryFactory
{
    public static VersionQuery create( final NodeVersionQuery query )
    {
        if ( query.getQuery() != null )
        {
            throw new IllegalArgumentException( "Unsupported version query construct: query expression " + query.getQuery() );
        }
        if ( query.getPostFilters().isNotEmpty() )
        {
            throw new IllegalArgumentException( "Unsupported version query construct: post filters " + query.getPostFilters() );
        }
        if ( query.getAggregationQueries().isNotEmpty() )
        {
            throw new IllegalArgumentException( "Unsupported version query construct: aggregations" );
        }
        if ( query.getSuggestionQueries().isNotEmpty() )
        {
            throw new IllegalArgumentException( "Unsupported version query construct: suggestions" );
        }
        if ( query.getHighlight() != null )
        {
            throw new IllegalArgumentException( "Unsupported version query construct: highlight" );
        }

        Instant tsFloor = null;
        Instant tsCeiling = null;
        String versionIdAfter = null;
        VersionQuery.BlobKeyTerm blobKeyTerm = null;
        VersionQuery.Cursor cursor = null;

        for ( final Filter filter : query.getQueryFilters() )
        {
            if ( filter instanceof final RangeFilter range )
            {
                if ( isField( range.getFieldName(), VersionIndexPath.TIMESTAMP ) )
                {
                    if ( range.getFrom() == null && range.getTo() == null )
                    {
                        throw new IllegalArgumentException( "Unsupported version query filter: unbounded " + range );
                    }
                    if ( range.getFrom() != null )
                    {
                        requireNoDuplicate( tsFloor, range );
                        if ( !range.isIncludeLower() )
                        {
                            throw new IllegalArgumentException( "Unsupported version query filter: exclusive timestamp floor " + range );
                        }
                        tsFloor = range.getFrom().asInstant();
                    }
                    if ( range.getTo() != null )
                    {
                        requireNoDuplicate( tsCeiling, range );
                        if ( !range.isIncludeUpper() )
                        {
                            throw new IllegalArgumentException( "Unsupported version query filter: exclusive timestamp ceiling " + range );
                        }
                        tsCeiling = range.getTo().asInstant();
                    }
                }
                else if ( isField( range.getFieldName(), VersionIndexPath.VERSION_ID ) )
                {
                    requireNoDuplicate( versionIdAfter, range );
                    if ( range.getFrom() == null || range.isIncludeLower() || range.getTo() != null )
                    {
                        throw new IllegalArgumentException( "Unsupported version query filter: " + range + " (only gt is supported)" );
                    }
                    versionIdAfter = range.getFrom().asString();
                }
                else
                {
                    throw new IllegalArgumentException( "Unsupported version query filter field: " + range );
                }
            }
            else if ( filter instanceof final ValueFilter value )
            {
                requireNoDuplicate( blobKeyTerm, value );
                blobKeyTerm = new VersionQuery.BlobKeyTerm( singleStringValue( value ), blobKeyField( value ) );
            }
            else if ( filter instanceof final BooleanFilter bool )
            {
                requireNoDuplicate( cursor, bool );
                cursor = toCursor( bool );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported version query filter: " + filter );
            }
        }

        final String nodeId = query.getNodeId() == null ? null : query.getNodeId().toString();

        return new VersionQuery( nodeId, tsFloor, tsCeiling, versionIdAfter, blobKeyTerm, cursor, toOrder( query.getOrderBys() ),
                                  query.getFrom(), query.getSize() );
    }

    /**
     * The keyset continuation GetNodeVersionsCommand emits, and nothing else:
     * {@code should(ts < X), should(ts = X AND versionid > Y)}.
     */
    private static VersionQuery.Cursor toCursor( final BooleanFilter filter )
    {
        if ( !filter.getMust().isEmpty() || !filter.getMustNot().isEmpty() || filter.getShould().size() != 2 )
        {
            throw new IllegalArgumentException( "Unsupported version query filter: " + filter + " (not a keyset cursor)" );
        }

        RangeFilter tsBefore = null;
        BooleanFilter tieBreaker = null;
        for ( final Filter should : filter.getShould() )
        {
            if ( should instanceof final RangeFilter range )
            {
                tsBefore = range;
            }
            else if ( should instanceof final BooleanFilter bool )
            {
                tieBreaker = bool;
            }
        }
        if ( tsBefore == null || tieBreaker == null || !isField( tsBefore.getFieldName(), VersionIndexPath.TIMESTAMP ) ||
            tsBefore.getTo() == null || tsBefore.isIncludeUpper() || tsBefore.getFrom() != null )
        {
            throw new IllegalArgumentException( "Unsupported version query filter: " + filter + " (not a keyset cursor)" );
        }
        if ( !tieBreaker.getShould().isEmpty() || !tieBreaker.getMustNot().isEmpty() || tieBreaker.getMust().size() != 2 ||
            !( tieBreaker.getMust().get( 0 ) instanceof final ValueFilter tsEquals ) ||
            !( tieBreaker.getMust().get( 1 ) instanceof final RangeFilter idAfter ) )
        {
            throw new IllegalArgumentException( "Unsupported version query filter: " + filter + " (not a keyset cursor)" );
        }

        final Instant ts = tsBefore.getTo().asInstant();
        if ( !isField( tsEquals.getFieldName(), VersionIndexPath.TIMESTAMP ) || tsEquals.getValues().size() != 1 ||
            !ts.equals( tsEquals.getValues().iterator().next().asInstant() ) )
        {
            throw new IllegalArgumentException( "Unsupported version query filter: " + filter + " (not a keyset cursor)" );
        }
        if ( !isField( idAfter.getFieldName(), VersionIndexPath.VERSION_ID ) || idAfter.getFrom() == null ||
            idAfter.isIncludeLower() || idAfter.getTo() != null )
        {
            throw new IllegalArgumentException( "Unsupported version query filter: " + filter + " (not a keyset cursor)" );
        }

        return new VersionQuery.Cursor( ts, idAfter.getFrom().asString() );
    }

    private static VersionQuery.Order toOrder( final List<OrderExpr> orderBys )
    {
        if ( orderBys.isEmpty() )
        {
            return VersionQuery.Order.UNORDERED;
        }
        if ( orderBys.size() == 1 && isFieldOrder( orderBys.get( 0 ), VersionIndexPath.VERSION_ID, OrderExpr.Direction.ASC ) )
        {
            return VersionQuery.Order.ID_ASC;
        }
        if ( isFieldOrder( orderBys.get( 0 ), VersionIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) &&
            ( orderBys.size() == 1 || orderBys.size() == 2 &&
                isFieldOrder( orderBys.get( 1 ), VersionIndexPath.VERSION_ID, OrderExpr.Direction.ASC ) ) )
        {
            return VersionQuery.Order.TS_DESC_ID_ASC;
        }
        throw new IllegalArgumentException( "Unsupported version query ordering: " + orderBys );
    }

    private static boolean isFieldOrder( final OrderExpr orderExpr, final IndexPath field, final OrderExpr.Direction direction )
    {
        return orderExpr instanceof final FieldOrderExpr fieldOrder && fieldOrder.getLanguage() == null &&
            isField( fieldOrder.getField().getIndexPath().getPath(), field ) && fieldOrder.getDirection() == direction;
    }

    private static VersionQuery.BlobKeyField blobKeyField( final ValueFilter filter )
    {
        if ( isField( filter.getFieldName(), VersionIndexPath.BINARY_BLOB_KEYS ) )
        {
            return VersionQuery.BlobKeyField.BINARY_KEYS;
        }
        if ( isField( filter.getFieldName(), VersionIndexPath.NODE_BLOB_KEY ) )
        {
            return VersionQuery.BlobKeyField.NODE_DATA_HASH;
        }
        throw new IllegalArgumentException( "Unsupported version query filter field: " + filter );
    }

    private static String singleStringValue( final ValueFilter filter )
    {
        if ( filter.getValues().size() != 1 )
        {
            throw new IllegalArgumentException( "Unsupported version query filter: " + filter + " (exactly one value expected)" );
        }
        return filter.getValues().iterator().next().asString();
    }

    private static boolean isField( final String fieldName, final IndexPath field )
    {
        return field.getPath().equalsIgnoreCase( fieldName );
    }

    private static void requireNoDuplicate( final Object existing, final Filter filter )
    {
        if ( existing != null )
        {
            throw new IllegalArgumentException( "Unsupported version query filter: duplicate predicate " + filter );
        }
    }
}
