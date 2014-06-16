package com.enonic.wem.portal.base;

import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.rendering.RenderResult;

public abstract class BaseHandler
{
    protected final RenderingMode parseMode( final String modeStr )
    {
        final RenderingMode mode = RenderingMode.from( modeStr );
        if ( mode == null )
        {
            throw notFound();
        }

        return mode;
    }

    protected final WebApplicationException notFound()
    {
        return new WebApplicationException( Response.Status.NOT_FOUND );
    }

    protected final Response toResponse( final RenderResult from )
    {
        final Response.ResponseBuilder builder = Response.status( from.getStatus() );

        if ( from.getEntity() != null )
        {
            builder.entity( from.getEntity() );
        }

        for ( final Map.Entry<String, String> header : from.getHeaders().entrySet() )
        {
            builder.header( header.getKey(), header.getValue() );
        }

        return builder.build();
    }
}
