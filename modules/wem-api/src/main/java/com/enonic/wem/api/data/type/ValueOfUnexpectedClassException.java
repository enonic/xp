package com.enonic.wem.api.data.type;


import com.enonic.wem.api.data.Value;

/**
 * Thrown if Value is not of expected type.
 */
public class ValueOfUnexpectedClassException
    extends RuntimeException
{
    public ValueOfUnexpectedClassException( final JavaTypeConverter javaTypeConverter, final Value value )
    {
        super( buildMessage( javaTypeConverter, value ) );
    }

    private static java.lang.String buildMessage( final JavaTypeConverter javaTypeConverter, final Value value )
    {
        return "Value object is not of expected class. Expected [" + javaTypeConverter.getType() + "], got: " +
            value.getType().getJavaTypeConverter().getType();
    }
}
