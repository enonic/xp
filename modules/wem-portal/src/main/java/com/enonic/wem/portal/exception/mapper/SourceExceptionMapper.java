package com.enonic.wem.portal.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Throwables;

import com.enonic.wem.portal.exception.renderer.ExceptionRenderer;
import com.enonic.wem.portal.script.SourceException;

@Provider
public final class SourceExceptionMapper
    implements ExceptionMapper<SourceException>
{
    @Override
    public Response toResponse( final SourceException e )
    {
        final ExceptionRenderer renderer = new ExceptionRenderer();
        renderer.status( Response.Status.INTERNAL_SERVER_ERROR );
        renderer.description( e.getMessage() );
        renderer.title( "Script evaluation error" );
        renderer.sourceError( e );
        renderer.exception( Throwables.getRootCause( e ) );
        return renderer.render();
    }
}
