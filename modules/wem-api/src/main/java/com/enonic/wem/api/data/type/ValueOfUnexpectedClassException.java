package com.enonic.wem.api.data.type;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

/**
 * Thrown if Value is not of expected type.
 */
public class ValueOfUnexpectedClassException
    extends RuntimeException
{
    public ValueOfUnexpectedClassException( final JavaTypeConverter javaTypeConverter, final Property property )
    {
        super( buildMessage( javaTypeConverter, property ) );
    }

    public ValueOfUnexpectedClassException( final JavaTypeConverter javaTypeConverter, final Value value )
    {
        super( buildMessage( javaTypeConverter, value ) );
    }

    private static java.lang.String buildMessage( final JavaTypeConverter javaTypeConverter, final Property property )
    {
        return "Value object of Property [" + property.getPath() + "] is not of expected class. Expected [" + javaTypeConverter.getType() +
            "], got: " + property.getValue().getType().getJavaTypeConverter().getType();
    }

    private static java.lang.String buildMessage( final JavaTypeConverter javaTypeConverter, final Value value )
    {
        return "Value object is not of expected class. Expected [" + javaTypeConverter.getType() + "], got: " +
            value.getType().getJavaTypeConverter().getType();
    }
}
