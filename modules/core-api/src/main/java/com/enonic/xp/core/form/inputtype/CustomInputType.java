package com.enonic.xp.core.form.inputtype;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.data.Value;

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
