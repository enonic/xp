package com.enonic.xp.exception;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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

    public NotFoundException( final String message, final Object... args )
    {
        super( message, args );
    }

    public NotFoundException( final Throwable cause, final String message, final Object... args )
    {
        super( cause, message, args );
    }
}
