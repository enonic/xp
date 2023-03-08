package com.enonic.xp.util;

import com.google.common.collect.Range;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class GeoPoint
{
    private static final Range<Double> LATITUDE_RANGE = Range.closed( -90.0, 90.0 );

    private static final Range<Double> LONGITUDE_RANGE = Range.closed( -180.0, 180.0 );

    private final String value;

    private final double latitude;

    private final double longitude;

    public GeoPoint( final double latitude, final double longitude )
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.value = String.format( "%s,%s", this.latitude, this.longitude );

        if ( !LATITUDE_RANGE.contains( this.latitude ) )
        {
            throw new IllegalArgumentException( String.format( "Latitude [%s] is not within range %s", this.latitude, LATITUDE_RANGE ) );
        }

        if ( !LONGITUDE_RANGE.contains( this.longitude ) )
        {
            throw new IllegalArgumentException( String.format( "Longitude [%s] is not within range %s", this.longitude, LONGITUDE_RANGE ) );
        }
    }

    public double getLatitude()
    {
        return this.latitude;
    }

    public double getLongitude()
    {
        return this.longitude;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof GeoPoint ) && ( (GeoPoint) o ).value.equals( this.value );
    }

    @Override
    public int hashCode()
    {
        return this.value.hashCode();
    }

    @Override
    public String toString()
    {
        return this.value;
    }

    public static GeoPoint from( final String value )
    {
        final String[] parts = value.split( ",", -1 );
        if ( parts.length != 2 )
        {
            throw new IllegalArgumentException( String.format( "Value [%s] is not a valid geo-point", value ) );
        }

        try
        {
            final double latitude = Double.parseDouble( parts[0].trim() );
            final double longitude = Double.parseDouble( parts[1].trim() );
            return new GeoPoint( latitude, longitude );
        }
        catch ( final NumberFormatException e )
        {
            throw new IllegalArgumentException( String.format( "Value [%s] is not a valid geo-point", value ) );
        }
    }
}
