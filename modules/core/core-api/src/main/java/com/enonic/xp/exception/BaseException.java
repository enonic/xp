package com.enonic.xp.exception;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class BaseException
    extends RuntimeException
{
    public BaseException( final String message )
    {
        super( message );
    }

    public BaseException( final Throwable t, final String message )
    {
        super( message, t );
    }

    public String getCode()
    {
        return "unknown";
    }
}
