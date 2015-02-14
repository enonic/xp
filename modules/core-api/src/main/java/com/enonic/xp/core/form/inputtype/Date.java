package com.enonic.xp.core.form.inputtype;


import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.data.ValueTypes;
import com.enonic.xp.core.form.BreaksRequiredContractException;

final class Date
    extends InputType
{
    Date()
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
        return Value.newLocalDate( ValueTypes.LOCAL_DATE.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }
}

