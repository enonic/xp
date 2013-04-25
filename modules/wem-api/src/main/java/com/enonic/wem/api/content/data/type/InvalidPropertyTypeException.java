package com.enonic.wem.api.content.data.type;

import com.enonic.wem.api.content.data.Property;

public class InvalidPropertyTypeException
    extends RuntimeException
{
    public InvalidPropertyTypeException( final Property property, final PropertyType expectedType )
    {
        super( buildMessage( property, expectedType ) );
    }

    private static String buildMessage( final Property property, final PropertyType expectedType )
    {
        return "Invalid property [" + property + "]. Type expected to be " + expectedType + ": " + property.getType();
    }
}
