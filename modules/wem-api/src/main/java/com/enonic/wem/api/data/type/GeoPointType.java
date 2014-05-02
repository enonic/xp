package com.enonic.wem.api.data.type;

import java.util.StringTokenizer;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.InvalidValueException;

public final class GeoPointType
    extends ValueType<String>
{
    private static final double LATITUDE_RANGE_START = -90.0;

    private static final double LATITUDE_RANGE_END = 90.0;

    private static final double LONGITUDE_RANGE_START = -180.0;

    private static final double LONGITUDE_RANGE_END = 180.0;

    public GeoPointType()
    {
        super( 10, "GeoPoint", JavaTypeConverters.STRING );
    }

    @Override
    public Value newValue( final Object value )
    {
        return Value.newGeoPoint( convert( value ) );
    }

    @Override
    public void checkValidity( final Value value )
        throws ValueOfUnexpectedClassException, InvalidValueException
    {
        super.checkValidity( value );

        final double[] geo = parse( value.toString() );
        final double latitude = geo[0];
        final double longitude = geo[1];

        if ( latitude < LATITUDE_RANGE_START || latitude > LATITUDE_RANGE_END )
        {
            throw new InvalidValueException( value, "latitude not within range from " + LATITUDE_RANGE_START + " to " +
                LATITUDE_RANGE_END );
        }

        if ( longitude < LONGITUDE_RANGE_START || longitude > LONGITUDE_RANGE_END )
        {
            throw new InvalidValueException( value, "longitude not within range from " + LONGITUDE_RANGE_START + " to " +
                LONGITUDE_RANGE_END );
        }
    }

    public static double getLatitude( final java.lang.String value )
    {
        return parse( value )[0];
    }

    public static double getLongitude( final java.lang.String value )
    {
        return parse( value )[1];
    }

    private static double[] parse( final java.lang.String str )
    {
        final StringTokenizer st = new StringTokenizer( str, "," );

        final double[] geo = new double[2];
        geo[0] = Double.parseDouble( st.nextToken() );
        geo[1] = Double.parseDouble( st.nextToken() );

        return geo;
    }
}
