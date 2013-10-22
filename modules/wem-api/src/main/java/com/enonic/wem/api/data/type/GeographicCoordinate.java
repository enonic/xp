package com.enonic.wem.api.data.type;


import java.util.StringTokenizer;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.InvalidValueException;

public class GeographicCoordinate
    extends ValueType<java.lang.String>
{
    private static final double LATITUDE_RANGE_START = -90.0;

    private static final double LATITUDE_RANGE_END = 90.0;

    private static final double LONGITUDE_RANGE_START = -180.0;

    private static final double LONGITUDE_RANGE_END = 180.0;

    GeographicCoordinate( int key )
    {
        super( key, JavaTypeConverter.String.GET );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.GeographicCoordinate( convert( value ) );
    }

    @Override
    public Property newProperty( final java.lang.String name, final Value value )
    {
        return new Property.GeographicCoordinate( name, value );
    }

    @Override
    public void checkValidity( final Value value )
        throws ValueOfUnexpectedClassException, InvalidValueException
    {
        super.checkValidity( value );

        final ValueHolder valueHolder = parse( value.getString() );
        if ( valueHolder.latitude < LATITUDE_RANGE_START || valueHolder.latitude > LATITUDE_RANGE_END )
        {
            throw new InvalidValueException( value,
                                             "latitude not within range from " + LATITUDE_RANGE_START + " to " + LATITUDE_RANGE_END );
        }

        if ( valueHolder.longitude < LONGITUDE_RANGE_START || valueHolder.longitude > LONGITUDE_RANGE_END )
        {
            throw new InvalidValueException( value,
                                             "longitude not within range from " + LONGITUDE_RANGE_START + " to " + LONGITUDE_RANGE_END );
        }
    }

    public static double getLatitude( final java.lang.String value )
    {
        return parse( value ).latitude;
    }

    public static double getLongitude( final java.lang.String value )
    {
        return parse( value ).longitude;
    }

    private static ValueHolder parse( final java.lang.String str )
    {
        final ValueHolder valueHolder = new ValueHolder();
        final StringTokenizer st = new StringTokenizer( str, "," );
        valueHolder.latitude = java.lang.Double.parseDouble( st.nextToken() );
        valueHolder.longitude = java.lang.Double.parseDouble( st.nextToken() );
        return valueHolder;
    }


    private static class ValueHolder
    {
        private java.lang.Double latitude;

        private java.lang.Double longitude;

    }
}
