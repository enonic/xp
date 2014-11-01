package com.enonic.wem.servlet.internal.exception;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.servlet.jaxrs.exception.ExceptionInfo;

@Provider
final class ExceptionInfoFilter
    implements ContainerResponseFilter
{
    @Context
    private HttpHeaders headers;

    @Override
    public void filter( final ContainerRequestContext req, final ContainerResponseContext res )
        throws IOException
    {
        final Object entity = res.getEntity();
        if ( !( entity instanceof ExceptionInfo ) )
        {
            return;
        }

        res.setEntity( entity, null, findMediaType() );
    }

    private MediaType findMediaType()
    {
        final List<MediaType> list = this.headers.getAcceptableMediaTypes();
        for ( final MediaType type : list )
        {
            if ( !type.isWildcardType() && type.isCompatible( MediaType.TEXT_HTML_TYPE ) )
            {
                return MediaType.TEXT_HTML_TYPE;
            }
        }

        return MediaType.APPLICATION_JSON_TYPE;
    }
}
