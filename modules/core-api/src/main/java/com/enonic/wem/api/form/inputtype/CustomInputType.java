package com.enonic.wem.api.form.inputtype;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

/* temporary solution for custom:className */
public class CustomInputType
    extends InputType
{
    private String className;

    public CustomInputType( String className )
    {
        this.className = className;
    }

    @Override
    public String getName()
    {
        return className;
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
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
