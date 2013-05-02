package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Value;

public class InvalidJavaTypeException
    extends RuntimeException
{
    public InvalidJavaTypeException( final JavaTypeConverters.JavaTypeConverter javaType, final Value value )
    {
        super( buildMessage( javaType, value ) );
    }

    private static String buildMessage( final JavaTypeConverters.JavaTypeConverter javaType, final Value value )
    {
        return "Expected Value of class " + javaType + ": " + value.getType().getJavaType();
    }
}
