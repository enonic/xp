package com.enonic.wem.api.exception;

import java.text.MessageFormat;

public abstract class BaseException
    extends RuntimeException
{
    public BaseException( final String message )
    {
        super( message );
    }

    public BaseException( final String message, final Object... args )
    {
        this( null, message, args );
    }

    public BaseException( final Throwable cause, final String message, final Object... args )
    {
        super( MessageFormat.format( message, args ), cause );
    }
}
