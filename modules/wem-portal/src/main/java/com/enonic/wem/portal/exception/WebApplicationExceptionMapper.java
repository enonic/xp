package com.enonic.wem.portal.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class WebApplicationExceptionMapper
    implements ExceptionMapper<WebApplicationException>
{
    @Override
    public Response toResponse( final WebApplicationException e )
    {
        final ExceptionRenderer renderer = new ExceptionRenderer();
        renderer.exception( e );
        renderer.status( e.getResponse().getStatus() );
        renderer.description( "An error occured with status code = " + e.getResponse().getStatus() + "." );

        if ( e instanceof MethodNotAllowedException )
        {
            renderer.title( "Method Not Allowed" );
        }
        else
        {
            final Response.Status status = Response.Status.fromStatusCode( e.getResponse().getStatus() );
            renderer.title( status.getReasonPhrase() );
        }

        return renderer.render();
    }
}
