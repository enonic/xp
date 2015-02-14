package com.acme;


import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.form.BreaksRequiredContractException;
import com.enonic.xp.core.form.inputtype.InputTypeConfig;
import com.enonic.xp.core.form.inputtype.InputTypeExtension;

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
