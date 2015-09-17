package com.enonic.wem.repo.internal.elasticsearch.query.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.wem.repo.internal.index.IndexValueNormalizer;
import com.enonic.xp.data.Value;
import com.enonic.xp.util.GeoPoint;

class AbstractQueryBuilderFactory
{
    static Object getValueAsType( Value value )
    {
        if ( value.isDateType() )
        {
            return value.asInstant();
        }

        if ( value.isNumericType() )
        {
            return value.asDouble();
        }

        if ( value.isGeoPoint() )
        {
            final GeoPoint geoPoint = value.asGeoPoint();
            return new org.elasticsearch.common.geo.GeoPoint( geoPoint.getLatitude(), geoPoint.getLongitude() );
        }

        return IndexValueNormalizer.normalize( value.toString() );
    }

    static QueryBuilder buildNotQuery( final QueryBuilder negated )
    {
        return QueryBuilders.boolQuery().mustNot( negated );
    }


}
