package com.enonic.wem.api.data.type;


import com.enonic.wem.api.data.Value;

public class InvalidJavaTypeConverterException
    extends RuntimeException
{
    public InvalidJavaTypeConverterException( final JavaTypeConverter javaTypeConverter, final Value value )
    {
        super( buildMessage( javaTypeConverter, value ) );
    }

    private static java.lang.String buildMessage( final JavaTypeConverter javaTypeConverter, final Value value )
    {
        return "Expected Value of class " + javaTypeConverter + ": " + value.getType().getJavaTypeConverter();
    }
}
