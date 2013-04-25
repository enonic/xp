package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;

public class InvalidValueTypeException
    extends Exception
{
    public InvalidValueTypeException( final JavaType.BaseType javaType, final Property property )
    {
        super( buildMessage( javaType, property ) );
    }

    private static String buildMessage( final JavaType.BaseType javaType, final Property property )
    {
        return "Invalid value type at path [" + property.getPath() + "] " + property.getObject().getClass() + ", expected " +
            javaType.getType();
    }
}
