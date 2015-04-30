package com.enonic.xp.form.inputtype;


import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;

@Beta
public class Time
    extends InputType
{
    public Time()
    {

    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public void checkTypeValidity( final Property property )
        throws InvalidTypeException
    {
        if ( !ValueTypes.LOCAL_TIME.equals( property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.LOCAL_TIME );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newLocalTime( ValueTypes.LOCAL_TIME.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newLocalTime( ValueTypes.LOCAL_TIME.convert( value ) );
    }
}

