package com.enonic.xp.portal;

import com.google.common.annotations.Beta;

import com.enonic.xp.web.HttpStatus;

@Beta
public final class PortalError
{
    private HttpStatus status;

    private String message;

    private Exception exception;

    private PortalRequest request;

    public HttpStatus getStatus()
    {
        return status;
    }

    public void setStatus( final HttpStatus status )
    {
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( final String message )
    {
        this.message = message;
    }

    public Exception getException()
    {
        return exception;
    }

    public void setException( final Exception exception )
    {
        this.exception = exception;
    }

    public PortalRequest getRequest()
    {
        return request;
    }

    public void setRequest( final PortalRequest request )
    {
        this.request = request;
    }
}
