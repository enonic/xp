package com.enonic.wem.portal.exception.mapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.portal.exception.renderer.ExceptionRenderer;
import com.enonic.wem.portal.jaxrs.ExtendedStatus;

@Provider
public final class WebApplicationExceptionMapper
    implements ExceptionMapper<WebApplicationException>
{
    @Override
    public Response toResponse( final WebApplicationException e )
    {
        final Response.StatusType status = ExtendedStatus.fromCode( e.getResponse().getStatus() );

        final ExceptionRenderer renderer = new ExceptionRenderer();
        renderer.status( status );
        renderer.title( status.getReasonPhrase() );
        renderer.exception( e );
        renderer.description( "An error occured with status code = " + status.getStatusCode() + "." );
        return renderer.render();
    }
}
