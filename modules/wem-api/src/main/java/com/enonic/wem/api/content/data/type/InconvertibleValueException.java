package com.enonic.wem.api.content.data.type;

public class InconvertibleValueException
    extends RuntimeException
{
    public InconvertibleValueException( final Object value, final ValueType valueType )
    {
        super( buildMessage( value, valueType.getJavaType() ) );
    }

    public InconvertibleValueException( final Object value, final JavaType.BaseType javaType )
    {
        super( buildMessage( value, javaType ) );
    }

    public InconvertibleValueException( final Object value, final ValueType valueType, final Exception e )
    {
        super( buildMessage( value, valueType.getJavaType() ), e );
    }

    private static String buildMessage( final Object value, final JavaType.BaseType javaType )
    {
        return "Value [" + value + "] of " + value.getClass() + " is not convertible to object of " + javaType.getType();
    }
}
