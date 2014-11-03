package com.enonic.wem.admin.app;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.servlet.jaxrs.exception.ErrorPageBuilder;

@Provider
public final class AdminExceptionMapper
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
        return new ErrorPageBuilder().
            cause( cause ).
            description( getDescription( status, cause ) ).
            status( status.getStatusCode() ).
            title( status.getReasonPhrase() ).
            build();
    }


    private String getDescription( final Response.Status status, final Throwable cause )
    {
        if ( cause != null )
        {
            return cause.getMessage() + " (" + cause.getClass().getName() + ")";
        }

        return status.getReasonPhrase();
    }
}
