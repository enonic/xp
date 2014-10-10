package com.enonic.wem.portal.internal.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.portal.internal.exception.renderer.ExceptionRenderer;

@Provider
public final class PortalExceptionMapper
    implements ExceptionMapper<Throwable>
{
    @Override
    public Response toResponse( final Throwable cause )
    {
        final Response.Status status = getStatus( cause );
        return toResponse( status, cause );
    }

    private Response.Status getStatus( final Throwable cause )
    {
        if ( cause instanceof WebApplicationException )
        {
            return getStatus( (WebApplicationException) cause );
        }

        if ( cause instanceof NodeNotFoundException )
        {
            return Response.Status.NOT_FOUND;
        }

        return Response.Status.INTERNAL_SERVER_ERROR;
    }

    private Response.Status getStatus( final WebApplicationException cause )
    {
        return Response.Status.fromStatusCode( cause.getResponse().getStatus() );
    }

    private Response toResponse( final Response.Status status, final Throwable cause )
    {
        final String html = renderThrowable( status, cause );
        return Response.status( status ).entity( html ).type( MediaType.TEXT_HTML_TYPE ).build();
    }

    private String renderThrowable( final Response.Status status, final Throwable cause )
    {
        if ( cause instanceof ResourceProblemException )
        {
            return renderSourceException( status, (ResourceProblemException) cause );
        }
        else
        {
            return renderOtherError( status, cause );
        }
    }

    private String renderSourceException( final Response.Status status, final ResourceProblemException cause )
    {
        return new ExceptionRenderer().
            sourceError( cause.getInnerError() ).
            exception( cause ).
            description( getDescription( status, cause.getInnerError() ) ).
            status( status.getStatusCode() ).
            title( "Script evaluation error" ).
            render();
    }

    private String renderOtherError( final Response.Status status, final Throwable cause )
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
            str = cause.getMessage() + " (" + cause.getClass().getName() + ")";
        }

        if ( str == null )
        {
            if ( cause != null )
            {
                str = cause.getClass().getName();
            }
            else
            {
                str = status.getReasonPhrase();
            }
        }

        return str;
    }
}
