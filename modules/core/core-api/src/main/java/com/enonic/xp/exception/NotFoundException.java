package com.enonic.xp.exception;

public abstract class NotFoundException
    extends BaseException
{
    public NotFoundException( final String message )
    {
        super( message );
    }

    public NotFoundException( final Throwable t, final String message )
    {
        super( t, message );
    }
}
