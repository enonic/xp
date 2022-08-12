package com.enonic.xp.lib.value;

import com.enonic.xp.util.GeoPoint;

public class GeoPointHandler
{
    public GeoPoint newInstance( double latitude, double longitude )
    {
        return new GeoPoint( latitude, longitude );
    }

    public GeoPoint from( String value )
    {
        return GeoPoint.from( value );
    }
}
