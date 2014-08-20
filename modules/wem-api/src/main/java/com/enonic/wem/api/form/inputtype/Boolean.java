package com.enonic.wem.api.form.inputtype;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.BreaksRequiredContractException;

public class Boolean
    extends InputType
{
    public Boolean()
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
        return Value.newBoolean( ValueTypes.BOOLEAN.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }
}