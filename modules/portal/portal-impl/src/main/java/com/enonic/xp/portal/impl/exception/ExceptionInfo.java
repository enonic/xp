package com.enonic.xp.portal.impl.exception;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.web.HttpStatus;

final class ExceptionInfo
{
    private final HttpStatus status;

    private String message;

    private Throwable cause;

    private ResourceService resourceService;

    private ExceptionInfo( final HttpStatus status )
    {
        this.status = status;
    }

    public HttpStatus getStatus()
    {
        return this.status;
    }

    public boolean shouldLogAsError()
    {
        return this.status.is5xxServerError() || this.status == HttpStatus.BAD_REQUEST;
    }

    public String getReasonPhrase()
    {
        return this.status.getReasonPhrase();
    }

    public String getMessage()
    {
        if ( this.message != null )
        {
            return this.message;
        }

        final String str = this.cause != null ? this.cause.getMessage() : null;
        return str != null ? str : getReasonPhrase();
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

    public PortalResponse toResponse( final PortalRequest req )
    {
        final String accept = Strings.nullToEmpty( req.getHeaders().get( HttpHeaders.ACCEPT ) );
        final boolean isHtml = accept.contains( "text/html" );
        return isHtml ? toHtmlResponse() : toJsonResponse();
    }

    private PortalResponse toJsonResponse()
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put( "status", this.status.value() );
        node.put( "message", getDescription() );

        return PortalResponse.create().
            status( this.status.value() ).
            body( node.toString() ).
            contentType( "application/json" ).
            build();
    }

    public PortalResponse toHtmlResponse()
    {
        final ErrorPageBuilder builder = new ErrorPageBuilder().
            cause( this.cause ).
            description( getDescription() ).
            resourceService( this.resourceService ).
            status( this.status.value() ).
            title( getReasonPhrase() );

        final String html = builder.build();
        return PortalResponse.create().
            status( this.status.value() ).
            body( html ).
            contentType( "text/html" ).
            build();
    }

    private String getDescription()
    {
        String str = getMessage();
        final Throwable cause = this.cause != null ? this.cause.getCause() : null;
        if ( cause != null )
        {
            str += " (" + cause.getClass().getName() + ")";
        }

        return str;
    }

    public static ExceptionInfo create( final HttpStatus status )
    {
        return new ExceptionInfo( status );
    }
}
