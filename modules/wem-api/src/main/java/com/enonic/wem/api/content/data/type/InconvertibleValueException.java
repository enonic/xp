package com.enonic.wem.api.content.data.type;

public class InconvertibleValueException
    extends RuntimeException
{
    public InconvertibleValueException( final Object value, final JavaTypeConverter javaTypeConverter )
    {
        super( buildMessage( value, javaTypeConverter ) );
    }

    private static String buildMessage( final Object value, final JavaTypeConverter javaTypeConverter )
    {
        return "Value [" + value + "] of " + value.getClass() + " is not convertible to object of " + javaTypeConverter.getType();
    }
}
