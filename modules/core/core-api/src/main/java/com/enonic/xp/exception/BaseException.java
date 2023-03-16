package com.enonic.xp.exception;

import java.text.MessageFormat;

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

    @Deprecated
    public BaseException( final String message, final Object... args )
    {
        this( null, message, args );
    }

    @Deprecated
    public BaseException( final Throwable cause, final String message, final Object... args )
    {
        super( message == null ? "" : MessageFormat.format( message, args ), cause );
    }

    public String getCode()
    {
        return "unknown";
    }
}
