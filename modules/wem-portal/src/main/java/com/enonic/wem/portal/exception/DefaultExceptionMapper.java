package com.enonic.wem.portal.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.api.exception.NotFoundException;
import com.enonic.wem.portal.exception.renderer.ExceptionRenderer;
import com.enonic.wem.portal.script.SourceException;

@Provider
public final class DefaultExceptionMapper
    implements ExceptionMapper<Throwable>
{
    @Override
    public Response toResponse( final Throwable e )
    {
        final Response.Status status = findStatus( e );
        final String html = render( status, e );
        return Response.status( status ).entity( html ).type( MediaType.TEXT_HTML_TYPE ).build();
    }

    private String render( final Response.Status status, final Throwable cause )
    {
        if ( cause instanceof SourceException )
        {
            return renderSourceException( status, (SourceException) cause );
        }

        return renderException( status, cause );
    }

    private Response.Status findStatus( final Throwable cause )
    {
        if ( cause instanceof WebApplicationException )
        {
            return findStatus( (WebApplicationException) cause );
        }

        if ( cause instanceof NotFoundException )
        {
            return Response.Status.NOT_FOUND;
        }

        return Response.Status.INTERNAL_SERVER_ERROR;
    }

    private Response.Status findStatus( final WebApplicationException cause )
    {
        final int code = cause.getResponse().getStatus();
        return Response.Status.fromStatusCode( code );
    }

    private String renderSourceException( final Response.Status status, final SourceException cause )
    {
        return new ExceptionRenderer().
            sourceError( cause.getInnerSourceError() ).
            exception( cause ).
            description( getDescription( status, cause ) ).
            status( status.getStatusCode() ).
            title( "Script evaluation error" ).
            render();
    }

    private String renderException( final Response.Status status, final Throwable cause )
    {
        return new ExceptionRenderer().
            exception( cause ).
            description( getDescription( status, cause ) ).
            status( status.getStatusCode() ).
            title( status.getReasonPhrase() ).
            render();
    }

    private String getDescription( final Response.Status status, final Throwable cause )
    {
        String str = null;

        if ( cause != null )
        {
            str = cause.getMessage();
        }

        if ( str == null )
        {
            str = status.getReasonPhrase();
        }

        return str;
    }
}
