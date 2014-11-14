package com.enonic.wem.core.elasticsearch.query.builder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.util.GeoPoint;
import com.enonic.wem.core.index.IndexValueNormalizer;

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
