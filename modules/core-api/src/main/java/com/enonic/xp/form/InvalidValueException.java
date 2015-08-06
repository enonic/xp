package com.enonic.xp.form;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;

@Beta
public final class InvalidValueException
    extends RuntimeException
{
    public InvalidValueException( Property property, final String message )
    {
        super( buildMessage( property, message ) );
    }

    private static String buildMessage( final Property property, final String message )
    {
        return "Invalid value in [" + property + "]: " + message + ": " + property.getObject();
    }
}
