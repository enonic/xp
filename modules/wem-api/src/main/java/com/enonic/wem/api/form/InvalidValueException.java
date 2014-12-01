package com.enonic.wem.api.form;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

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
