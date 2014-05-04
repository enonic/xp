package com.enonic.wem.api.converter;

public final class ConvertException
    extends RuntimeException
{
    public ConvertException( final String message, final Object... args )
    {
        super( String.format( message, args ) );
    }
}
