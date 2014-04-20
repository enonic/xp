package com.enonic.wem.portal.exception.renderer;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.portal.script.SourceException;

public final class ExceptionRenderer
{
    private final static ExceptionTemplate TEMPLATE = new ExceptionTemplate();

    private Response.StatusType status;

    private final StatusErrorInfo info;

    public ExceptionRenderer()
    {
        this.info = new StatusErrorInfo();
    }

    public ExceptionRenderer status( final Response.StatusType status )
    {
        this.status = status;
        this.info.statusCode( this.status.getStatusCode() );
        return this;
    }

    public ExceptionRenderer title( final String value )
    {
        this.info.title( value );
        return this;
    }

    public ExceptionRenderer description( final String value )
    {
        this.info.description( value );
        return this;
    }

    public ExceptionRenderer exception( final Throwable e )
    {
        this.info.cause( new CauseInfo( e ) );
        return this;
    }

    public ExceptionRenderer sourceError( final SourceException error )
    {
        this.info.source( new SourceInfo( error ) );
        this.info.callStack( new CallStackInfo( error ) );
        return this;
    }

    public Response render()
    {
        final String str = TEMPLATE.render( this.info );
        return Response.status( this.status ).entity( str ).type( MediaType.TEXT_HTML_TYPE ).build();
    }
}
