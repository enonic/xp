package com.enonic.xp.data;

public final class ValueTypeException
    extends RuntimeException
{
    public ValueTypeException( final String message, final Object... args )
    {
        super( String.format( message, args ) );
    }
}
