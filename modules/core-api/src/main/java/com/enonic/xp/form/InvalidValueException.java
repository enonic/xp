package com.enonic.xp.form;


import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;

@Beta
public class InvalidValueException
    extends RuntimeException
{
    public InvalidValueException( Property property, final String message )
    {
        super( buildMessage( property, message ) );
    }

    public InvalidValueException( Value value, final String message )
    {
        super( buildMessage( value, message ) );
    }

    private static String buildMessage( final Property property, final String message )
    {
        return "Invalid value in [" + property + "]: " + message + ": " + property.getObject();
    }

    private static String buildMessage( final Value value, final String message )
    {
        return "Invalid value: " + message + ": " + value.getObject();
    }
}
