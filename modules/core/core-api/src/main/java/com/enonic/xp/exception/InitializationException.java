package com.enonic.xp.exception;

public final class InitializationException
    extends BaseException
{
    public InitializationException( final String message )
    {
        super( message );
    }

    public InitializationException( final Throwable cause, final String message )
    {
        super( cause, message );
    }
}
