package com.enonic.wem.portal.exception.renderer;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.portal.script.SourceException;
import com.enonic.wem.web.mvc.FreeMarkerView;

public final class ExceptionRenderer
{
    private final FreeMarkerView view;

    private Response.StatusType status;

    public ExceptionRenderer()
    {
        this.view = FreeMarkerView.template( "portalError.ftl" );
    }

    public ExceptionRenderer status( final Response.StatusType status )
    {
        this.status = status;
        this.view.put( "status", this.status );
        return this;
    }

    public ExceptionRenderer title( final String value )
    {
        this.view.put( "title", value );
        return this;
    }

    public ExceptionRenderer description( final String value )
    {
        this.view.put( "description", value );
        return this;
    }

    public ExceptionRenderer exception( final Throwable e )
    {
        this.view.put( "exception", new ExceptionInfo( e ) );
        return this;
    }

    public ExceptionRenderer sourceError( final SourceException error )
    {
        final ScriptSourceInfo info = new ScriptSourceInfo( error );
        this.view.put( "source", info );
        return this;
    }

    public Response render()
    {
        return Response.status( this.status ).entity( view ).type( MediaType.TEXT_HTML_TYPE ).build();
    }
}
