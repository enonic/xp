package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;

public class InvalidValueTypeException
    extends RuntimeException
{
    public InvalidValueTypeException( final JavaType.BaseType javaType, final Property property )
    {
        super( buildMessage( javaType, property ) );
    }

    public InvalidValueTypeException( final JavaType.BaseType javaType, final Value value )
    {
        super( buildMessage( javaType, value ) );
    }

    private static String buildMessage( final JavaType.BaseType javaType, final Property property )
    {
        return "Invalid ValueType at path [" + property.getPath() + "] " + property.getObject().getClass() + ", expected " +
            javaType.getType();
    }

    private static String buildMessage( final JavaType.BaseType javaType, final Value value )
    {
        return "Expected Value of type " + javaType + ": " + value.getType().getJavaType();
    }
}
