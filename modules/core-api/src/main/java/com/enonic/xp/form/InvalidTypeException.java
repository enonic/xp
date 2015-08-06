package com.enonic.xp.form;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.ValueType;

@Beta
public final class InvalidTypeException
    extends RuntimeException
{
    public InvalidTypeException( final Property property, final ValueType valueType )
    {
        super( buildMessage( property, valueType ) );
    }

    private static String buildMessage( final Property property, final ValueType valueType )
    {
        return "Invalid type in [" + property + "]: " + property.getType() + ", instead of: " + valueType;
    }
}
