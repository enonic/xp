package com.enonic.xp.data;

import com.enonic.xp.util.GeoPoint;

final class GeoPointValueType
    extends ValueType<GeoPoint>
{
    GeoPointValueType()
    {
        super( "GeoPoint", JavaTypeConverters.GEO_POINT );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newGeoPoint( convertNullSafe( object ) );
    }
}
