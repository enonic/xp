package com.enonic.wem.portal.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.portal.exception.PortalWebException;
import com.enonic.wem.portal.exception.renderer.ExceptionRenderer;

@Provider
public final class PortalWebExceptionMapper
    implements ExceptionMapper<PortalWebException>
{
    @Override
    public Response toResponse( final PortalWebException e )
    {
        final ExceptionRenderer renderer = new ExceptionRenderer();
        renderer.exception( e );
        renderer.status( e.getStatus() );
        renderer.title( e.getStatus().getReasonPhrase() );

        String description = e.getDescription();
        if ( description == null )
        {
            description = "An error occured with status code = " + e.getStatus().getStatusCode() + ".";
        }

        renderer.description( description );
        return renderer.render();
    }
}
