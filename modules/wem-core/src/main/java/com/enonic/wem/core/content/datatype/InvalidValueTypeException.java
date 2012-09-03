package com.enonic.wem.core.content.datatype;


public class InvalidValueTypeException
    extends RuntimeException
{
    public InvalidValueTypeException( final JavaType javaType, final Object value )
    {
        super( buildMessage( javaType, value ) );
    }

    private static String buildMessage( final JavaType javaType, final Object value )
    {
        return "Invalid value type " + value.getClass() + ", expected  " + javaType.getValue();
    }
}
