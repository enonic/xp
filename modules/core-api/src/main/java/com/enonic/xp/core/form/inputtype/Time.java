package com.enonic.xp.core.form.inputtype;


import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.data.ValueTypes;
import com.enonic.xp.core.form.BreaksRequiredContractException;

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
    public Value newValue( final String value )
    {
        return Value.newLocalTime( ValueTypes.LOCAL_TIME.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }
}
