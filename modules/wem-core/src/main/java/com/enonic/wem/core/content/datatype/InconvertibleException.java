package com.enonic.wem.core.content.datatype;

public class InconvertibleException
    extends RuntimeException
{
    public InconvertibleException( final Object value, final DataType dataType )
    {
        super( buildMessage( value, dataType ) );
    }

    public InconvertibleException( final Object value, final DataType dataType, final Exception e )
    {
        super( buildMessage( value, dataType ), e );
    }

    private static String buildMessage( final Object value, final DataType dataType )
    {
        return "Value [" + value + "] of " + value.getClass() + " is not convertible to object of " + dataType.getJavaType().getValue();
    }
}
