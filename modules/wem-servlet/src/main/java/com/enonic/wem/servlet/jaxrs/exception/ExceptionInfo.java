package com.enonic.wem.servlet.jaxrs.exception;

import javax.ws.rs.core.Response;

public final class ExceptionInfo
{
    private final int status;

    private String message;

    private Throwable cause;

    private ExceptionInfo( final int status )
    {
        this.status = status;
    }

    public int getStatus()
    {
        return this.status;
    }

    public boolean shouldLogAsError()
    {
        if ( ( this.status >= 500 ) && ( this.status < 600 ) )
        {
            return true;
        }

        if ( this.status == 400 )
        {
            return true;
        }

        return false;
    }

    public String getReasonPhrase()
    {
        return Response.Status.fromStatusCode( this.status ).getReasonPhrase();
    }

    public String getMessage()
    {
        if ( this.message != null )
        {
            return this.message;
        }

        final String str = this.cause != null ? this.cause.getMessage() : null;
        return str != null ? str : "No message";
    }

    public ExceptionInfo message( final String message )
    {
        this.message = message;
        return this;
    }

    public Throwable getCause()
    {
        return this.cause;
    }

    public ExceptionInfo cause( final Throwable cause )
    {
        this.cause = cause;
        return this;
    }

    public Response toResponse()
    {
        return Response.status( this.status ).entity( this ).build();
    }

    public static ExceptionInfo create( final int status )
    {
        return new ExceptionInfo( status );
    }

    public static ExceptionInfo create( final Response.Status status )
    {
        return new ExceptionInfo( status.getStatusCode() );
    }
}
