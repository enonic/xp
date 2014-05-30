package com.enonic.wem.api.exception;


public class ConflictException
    extends BaseException
{
    public ConflictException( final String message )
    {
        super( message );
    }

    public ConflictException( final Throwable t, final String message )
    {
        super( t, message );
    }

    public ConflictException( final String message, final Object... args )
    {
        super( message, args );
    }

    public ConflictException( final Throwable cause, final String message, final Object... args )
    {
        super( cause, message, args );
    }
}
