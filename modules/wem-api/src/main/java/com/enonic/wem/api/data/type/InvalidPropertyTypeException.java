package com.enonic.wem.api.data.type;

import com.enonic.wem.api.data.Property;

public class InvalidPropertyTypeException
    extends RuntimeException
{
    public InvalidPropertyTypeException( final Property property, final ValueType expectedType )
    {
        super( buildMessage( property, expectedType ) );
    }

    private static java.lang.String buildMessage( final Property property, final ValueType expectedType )
    {
        return "Invalid property [" + property + "]. Type expected to be " + expectedType + ": " + property.getValueType();
    }
}
