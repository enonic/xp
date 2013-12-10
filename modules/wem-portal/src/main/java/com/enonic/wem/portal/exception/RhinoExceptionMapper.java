package com.enonic.wem.portal.exception;

import java.nio.file.Paths;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.mozilla.javascript.RhinoException;

@Provider
public final class RhinoExceptionMapper
    implements ExceptionMapper<RhinoException>
{
    @Override
    public Response toResponse( final RhinoException e )
    {
        final ExceptionRenderer renderer = new ExceptionRenderer();
        renderer.status( Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() );
        renderer.title( "Compilation error" );
        renderer.description( e.details() );
        renderer.source( Paths.get( e.sourceName() ), e.lineNumber(), e.columnNumber() );
        return renderer.render();
    }
}
