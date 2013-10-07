package com.enonic.wem.api.schema.content.form.inputtype;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.InvalidValueTypeException;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;

final class Color
    extends InputType
{
    Color()
    {
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final String stringValue = (String) property.getObject();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        final Value value = property.getValue();

        try
        {
            parse( value.getString() );
        }
        catch ( Exception e )
        {
            throw new InvalidValueException( property, e.getMessage() );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return new Value.String( value );
    }

    private static ValueHolder parse( final String str )
    {
        final ValueHolder valueHolder = new ValueHolder();
        List<String> strValues = Lists.newArrayList( Splitter.on( ";" ).split( str ) );

        valueHolder.setRed( parseInt( strValues.get( 0 ), "red" ) );
        valueHolder.setGreen( parseInt( strValues.get( 1 ), "green" ) );
        valueHolder.setBlue( parseInt( strValues.get( 2 ), "blue" ) );
        return valueHolder;
    }

    private static int parseInt( final String s, final String name )
    {
        try
        {
            return Integer.parseInt( s );
        }
        catch ( NumberFormatException e )
        {
            throw new IllegalArgumentException( "Integer value for color " + name + " not given: " + s );
        }
    }

    private static class ValueHolder
    {
        private int red;

        private int green;

        private int blue;

        void setRed( final int red )
        {
            checkRange( red, "red" );
            this.red = red;
        }

        void setGreen( final int green )
        {
            checkRange( green, "green" );
            this.green = green;
        }

        void setBlue( final int blue )
        {
            checkRange( blue, "blue" );
            this.blue = blue;
        }

        private int getRed()
        {
            return red;
        }

        private int getGreen()
        {
            return green;
        }

        private int getBlue()
        {
            return blue;
        }

        private void checkRange( final int range, final String color )
        {
            if ( range < 0 || range > 255 )
            {
                throw new IllegalArgumentException( "Value of color " + color + " must be between 0 and 255" );
            }
        }
    }
}
