package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.form.InvalidValueException;

public class NullConfig
    implements InputTypeConfig
{

    private NullConfig()
    {
    }

    public static NullConfig create()
    {
        return new NullConfig();
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueException
    {
        // No validation
    }
}
