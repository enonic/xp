package com.enonic.wem.api.content.data.datatype;

public class InconvertibleValueException
    extends RuntimeException
{
    public InconvertibleValueException( final Object value, final DataType dataType )
    {
        super( buildMessage( value, dataType.getJavaType() ) );
    }

    public InconvertibleValueException( final Object value, final JavaType.BaseType javaType )
    {
        super( buildMessage( value, javaType ) );
    }

    public InconvertibleValueException( final Object value, final DataType dataType, final Exception e )
    {
        super( buildMessage( value, dataType.getJavaType() ), e );
    }

    private static String buildMessage( final Object value, final JavaType.BaseType javaType )
    {
        return "Value [" + value + "] of " + value.getClass() + " is not convertible to object of " + javaType.getType();
    }
}
