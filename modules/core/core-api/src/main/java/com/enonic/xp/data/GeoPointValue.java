package com.enonic.xp.data;

import com.enonic.xp.util.GeoPoint;

final class GeoPointValue
    extends Value
{
    GeoPointValue( final GeoPoint value )
    {
        super( ValueTypes.GEO_POINT, value );
    }

    GeoPointValue( final GeoPointValue source )
    {
        super( ValueTypes.GEO_POINT, source.getObject() );
    }

    @Override
    Value copy( final PropertyTree tree )
    {
        return new GeoPointValue( this );
    }

    @Override
    Object toJsonValue()
    {
        return asString();
    }
}
