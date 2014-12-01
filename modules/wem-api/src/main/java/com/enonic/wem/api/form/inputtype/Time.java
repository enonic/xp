package com.enonic.wem.api.form.inputtype;


import com.enonic.wem.api.data2.Property;
import com.enonic.wem.api.data2.Value;
import com.enonic.wem.api.data2.ValueTypes;
import com.enonic.wem.api.form.BreaksRequiredContractException;

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
