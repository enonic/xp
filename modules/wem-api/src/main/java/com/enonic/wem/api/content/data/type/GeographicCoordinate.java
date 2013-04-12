package com.enonic.wem.api.content.data.type;


import java.util.StringTokenizer;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

public class GeographicCoordinate
    extends BaseDataType
{
    private static final double LATITUDE_RANGE_START = -90.0;

    private static final double LATITUDE_RANGE_END = 90.0;

    private static final double LONGITUDE_RANGE_START = -180.0;

    private static final double LONGITUDE_RANGE_END = 180.0;

    GeographicCoordinate( int key )
    {
        super( key, JavaType.STRING );
    }

    @Override
    public Value newValue( final Object value )
    {
        return new Value.GeographicCoordinate( JavaType.STRING.convertFrom( value ) );
    }

    @Override
    public Value.AbstractValueBuilder<Value.GeographicCoordinate, String> newValueBuilder()
    {
        return new Value.GeographicCoordinate.ValueBuilder();
    }

    @Override
    public Data newData( final String name, final Value value )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        checkValueIsOfExpectedJavaClass( data );

        final Value value = data.getValue();

        final ValueHolder valueHolder = parse( value.getString() );
        if ( valueHolder.latitude < LATITUDE_RANGE_START || valueHolder.latitude > LATITUDE_RANGE_END )
        {
            throw new InvalidValueException( data, "Value not within range from " + LATITUDE_RANGE_START + " to " + LATITUDE_RANGE_END );
        }

        if ( valueHolder.longitude < LONGITUDE_RANGE_START || valueHolder.longitude > LONGITUDE_RANGE_END )
        {
            throw new InvalidValueException( data, "Value not within range from " + LONGITUDE_RANGE_START + " to " + LONGITUDE_RANGE_END );
        }
    }

    public static double getLatitude( final String value )
    {
        return parse( value ).latitude;
    }

    public static double getLongitude( final String value )
    {
        return parse( value ).longitude;
    }

    private static ValueHolder parse( final String str )
    {
        final ValueHolder valueHolder = new ValueHolder();
        final StringTokenizer st = new StringTokenizer( str, "," );
        valueHolder.latitude = Double.parseDouble( st.nextToken() );
        valueHolder.longitude = Double.parseDouble( st.nextToken() );
        return valueHolder;
    }


    private static class ValueHolder
    {
        private Double latitude;

        private Double longitude;

    }
}
