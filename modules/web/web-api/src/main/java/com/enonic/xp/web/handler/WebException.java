package com.enonic.xp.web.handler;

import com.enonic.xp.web.HttpStatus;

public final class WebException
    extends RuntimeException
{
    private final HttpStatus status;

    public WebException( final HttpStatus status, final String message )
    {
        super( message );
        this.status = status;
    }

    public WebException( final HttpStatus status, final Throwable cause )
    {
        super( cause.getMessage(), cause );
        this.status = status;
    }

    public HttpStatus getStatus()
    {
        return this.status;
    }
}
