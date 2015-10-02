package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.geodistance.GeoDistanceBuilder;

import com.google.common.base.Strings;

import com.enonic.xp.query.aggregation.DistanceRange;
import com.enonic.xp.query.aggregation.GeoDistanceAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.index.IndexValueType;

class GeoDistanceAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public GeoDistanceAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AggregationBuilder create( final GeoDistanceAggregationQuery query )
    {
        final String fieldName = fieldNameResolver.resolve( query.getFieldName(), IndexValueType.GEO_POINT );

        final GeoDistanceBuilder geoDistanceBuilder = new GeoDistanceBuilder( query.getName() ).
            field( fieldName ).
            lat( query.getOrigin().getLatitude() ).
            lon( query.getOrigin().getLongitude() );

        if ( !Strings.isNullOrEmpty( query.getUnit() ) )
        {
            geoDistanceBuilder.unit( DistanceUnit.fromString( query.getUnit() ) );
        }

        for ( final DistanceRange range : query.getRanges() )
        {
            final String key = range.getKey();

            if ( range.getFrom() == null )
            {
                addUnboundedTo( geoDistanceBuilder, range, key );
            }
            else if ( range.getTo() == null )
            {
                addUnboundedFrom( geoDistanceBuilder, range, key );
            }
            else
            {
                addBoundedToAndFrom( geoDistanceBuilder, range, key );
            }
        }
        return geoDistanceBuilder;
    }

    private void addBoundedToAndFrom( final GeoDistanceBuilder geoDistanceBuilder, final DistanceRange range, final String key )
    {
        if ( Strings.isNullOrEmpty( key ) )
        {
            geoDistanceBuilder.addRange( range.getFrom(), range.getTo() );
        }
        else
        {
            geoDistanceBuilder.addRange( key, range.getFrom(), range.getTo() );
        }
    }

    private void addUnboundedFrom( final GeoDistanceBuilder geoDistanceBuilder, final DistanceRange range, final String key )
    {
        if ( Strings.isNullOrEmpty( key ) )
        {
            geoDistanceBuilder.addUnboundedFrom( range.getFrom() );
        }
        else
        {
            geoDistanceBuilder.addUnboundedFrom( key, range.getFrom() );
        }
    }

    private void addUnboundedTo( final GeoDistanceBuilder geoDistanceBuilder, final DistanceRange range, final String key )
    {
        if ( Strings.isNullOrEmpty( key ) )
        {
            geoDistanceBuilder.addUnboundedTo( range.getTo() );
        }
        else
        {
            geoDistanceBuilder.addUnboundedTo( key, range.getTo() );
        }
    }

}
