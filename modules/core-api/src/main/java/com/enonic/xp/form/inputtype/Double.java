package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;

final class Double
    extends InputType
{
    Double()
    {
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final java.lang.Double doubleValue = property.getDouble();
        if ( doubleValue == null )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public void checkTypeValidity( final Property property )
        throws InvalidTypeException
    {
        if ( !ValueTypes.DOUBLE.equals( property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.DOUBLE );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newDouble( ValueTypes.DOUBLE.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newDouble( ValueTypes.DOUBLE.convert( value ) );
    }
}
