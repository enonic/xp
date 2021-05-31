package com.enonic.xp.portal.impl.exception;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

import static com.google.common.base.Strings.nullToEmpty;

final class ExceptionInfo
{
    private final HttpStatus status;

    private String tip;

    private WebException cause;

    private ResourceService resourceService;

    private RunMode runMode;

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
        return cause.isLoggable();
    }

    public String getReasonPhrase()
    {
        return this.status.getReasonPhrase();
    }

    public String getMessage()
    {
        final String str = this.cause != null ? this.cause.getMessage() : null;
        return str != null ? str : getReasonPhrase();
    }

    public ExceptionInfo tip( final String tip )
    {
        this.tip = tip;
        return this;
    }

    public ExceptionInfo runMode( final RunMode runMode )
    {
        this.runMode = runMode;
        return this;
    }

    public WebException getCause()
    {
        return this.cause;
    }

    public ExceptionInfo cause( final WebException cause )
    {
        this.cause = cause;
        return this;
    }

    public ExceptionInfo resourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        return this;
    }

    public PortalResponse toResponse( final WebRequest req )
    {
        final String accept = nullToEmpty( req.getHeaders().get( HttpHeaders.ACCEPT ) );
        final boolean isHtml = accept.contains( "text/html" );
        return isHtml ? toHtmlResponse() : toJsonResponse();
    }

    private PortalResponse toJsonResponse()
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put( "status", this.status.value() );
        node.put( "message", getDescription() );

        return PortalResponse.create().
            status( this.status ).
            body( node.toString() ).
            contentType( MediaType.create( "application", "json" ) ).
            build();
    }

    public PortalResponse toHtmlResponse()
    {
        final ErrorPageBuilder builder;
        if ( runMode == RunMode.DEV )
        {
            builder = new ErrorPageRichBuilder().
                cause( this.cause ).
                description( getDescription() ).
                resourceService( this.resourceService ).
                status( this.status.value() ).
                title( getReasonPhrase() );
        }
        else
        {
            builder = new ErrorPageSimpleBuilder().status( this.status.value() ).tip( tip ).title( getReasonPhrase() );
        }

        final String html = builder.build();
        return PortalResponse.create().
            status( this.status ).
            body( html ).
            contentType( MediaType.create( "text", "html" ) ).
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
