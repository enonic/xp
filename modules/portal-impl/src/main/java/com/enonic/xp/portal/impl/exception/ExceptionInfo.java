package com.enonic.xp.portal.impl.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.xp.resource.ResourceService;

final class ExceptionInfo
{
    private final int status;

    private String message;

    private Throwable cause;

    private ResourceService resourceService;

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

    public ExceptionInfo resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return this;
    }

    public Response toResponse()
    {
        final ErrorPageBuilder builder = new ErrorPageBuilder().
            cause( this.cause ).
            description( getDescription() ).
            resourceService( this.resourceService ).
            status( this.status ).
            title( getReasonPhrase() );

        final String html = builder.build();
        return Response.status( this.status ).entity( html ).type( MediaType.TEXT_HTML_TYPE ).build();
    }

    private String getDescription()
    {
        String str = getMessage();
        if ( this.cause != null )
        {
            str += " (" + this.cause.getClass().getName() + ")";
        }

        return str;
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
