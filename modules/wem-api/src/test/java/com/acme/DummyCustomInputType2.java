package com.acme;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.InvalidValueTypeException;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypeConfig;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypeExtension;

public class DummyCustomInputType2
    extends InputTypeExtension
{
    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
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
        return null;
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }
}
