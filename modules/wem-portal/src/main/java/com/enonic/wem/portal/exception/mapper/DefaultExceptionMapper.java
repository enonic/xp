package com.enonic.wem.portal.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.portal.exception.renderer.ExceptionRenderer;

@Provider
public final class DefaultExceptionMapper
    implements ExceptionMapper<Throwable>
{
    @Override
    public Response toResponse( final Throwable e )
    {
        final ExceptionRenderer renderer = new ExceptionRenderer();
        renderer.status( Response.Status.INTERNAL_SERVER_ERROR );
        renderer.title( "An error occured" );
        renderer.description( e.getMessage() );
        renderer.exception( e );
        return renderer.render();
    }
}
