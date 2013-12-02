package com.enonic.wem.core.index.query;


import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.GeoPoint;

public class AbstractBuilderFactory
{
    Object getValueAsType( Value value )
    {
        if ( value.isDateType() )
        {
            return value.asDateTime();
        }

        if ( value.isNumericType() )
        {
            return value.asDouble();
        }

        if ( value.isGeoPoint() )
        {
            final double latitude = GeoPoint.getLatitude( value.asString() );
            final double longitude = GeoPoint.getLongitude( value.asString() );

            return new org.elasticsearch.common.geo.GeoPoint( latitude, longitude );
        }

        return value.asString();
    }

    QueryBuilder buildNotQuery( final QueryBuilder negated )
    {
        return QueryBuilders.boolQuery().mustNot( negated );
    }

}
