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
        renderer.status( e.getResponse().getStatus() );
        renderer.title( "An error occured" );
        renderer.description( e.getMessage() );
        renderer.exception( e );
        return renderer.render();
    }
}
