package com.enonic.wem.repo.internal.elasticsearch.query.translator;

import com.enonic.wem.repo.internal.index.IndexValueNormalizer;
import com.enonic.xp.data.Value;
import com.enonic.xp.util.GeoPoint;

public class ValueHelper
{
    public static Object getValueAsType( Value value )
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
}
