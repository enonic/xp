package com.enonic.wem.api.data.data.type;


import com.enonic.wem.api.data.data.Property;
import com.enonic.wem.api.data.data.Value;

public class InvalidValueTypeException
    extends RuntimeException
{
    public InvalidValueTypeException( final JavaTypeConverter javaTypeConverter, final Property property )
    {
        super( buildMessage( javaTypeConverter, property ) );
    }

    public InvalidValueTypeException( final JavaTypeConverter javaTypeConverter, final Value value )
    {
        super( buildMessage( javaTypeConverter, value ) );
    }

    private static String buildMessage( final JavaTypeConverter javaTypeConverter, final Property property )
    {
        return "Invalid ValueType at path [" + property.getPath() + "] " + property.getObject().getClass() + ", expected " +
            javaTypeConverter.getType();
    }

    private static String buildMessage( final JavaTypeConverter javaTypeConverter, final Value value )
    {
        return "Expected Value of type " + javaTypeConverter + ": " + value.getType().getJavaTypeConverter();
    }
}
