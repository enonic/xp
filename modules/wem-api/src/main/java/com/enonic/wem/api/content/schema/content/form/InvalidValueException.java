package com.enonic.wem.api.content.schema.content.form;


import com.enonic.wem.api.content.data.Property;

public class InvalidValueException
    extends Exception
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
