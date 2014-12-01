package com.acme;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.BreaksRequiredContractException;
import com.enonic.wem.api.form.inputtype.InputTypeConfig;
import com.enonic.wem.api.form.inputtype.InputTypeExtension;

public class DummyCustomInputType
    extends InputTypeExtension
{

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
