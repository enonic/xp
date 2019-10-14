package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.GeoDistanceAggregationBuilder;

import com.google.common.base.Strings;

import com.enonic.xp.query.aggregation.DistanceRange;
import com.enonic.xp.query.aggregation.GeoDistanceAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

class GeoDistanceAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    public GeoDistanceAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AbstractAggregationBuilder create( final GeoDistanceAggregationQuery query )
    {
        final String fieldName = fieldNameResolver.resolve( query.getFieldName(), IndexValueType.GEO_POINT );

        final GeoDistanceAggregationBuilder geoDistanceBuilder = AggregationBuilders.geoDistance( query.getName(), new GeoPoint(
            query.getOrigin().getLatitude(), query.getOrigin().getLongitude() ) ).
            field( fieldName );

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

    private void addBoundedToAndFrom( final GeoDistanceAggregationBuilder geoDistanceBuilder, final DistanceRange range, final String key )
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

    private void addUnboundedFrom( final GeoDistanceAggregationBuilder geoDistanceBuilder, final DistanceRange range, final String key )
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

    private void addUnboundedTo( final GeoDistanceAggregationBuilder geoDistanceBuilder, final DistanceRange range, final String key )
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
