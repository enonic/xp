package com.enonic.wem.api.value;

public final class ValueException
    extends RuntimeException
{
    public ValueException( final String message, final Object... args )
    {
        super( String.format( message, args ) );
    }
}
