package com.enonic.wem.portal.internal.base;

import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.google.common.base.Joiner;

import com.enonic.wem.portal.RenderMode;
import com.enonic.wem.portal.internal.rendering.RenderResult;

public abstract class BaseResource
{
    protected final RenderMode parseMode( final String mode )
    {
        final RenderMode parsed = RenderMode.from( mode );
        if ( parsed != null )
        {
            return parsed;
        }

        throw illegalMode( mode );
    }

    protected final WebApplicationException notFound( final String message, final Object... args )
    {
        return new WebApplicationException( String.format( message, args ), Response.Status.NOT_FOUND );
    }

    protected final Response toResponse( final RenderResult result )
    {
        final Response.ResponseBuilder builder = Response.status( result.getStatus() );
        builder.type( result.getType() );

        for ( final Map.Entry<String, String> header : result.getHeaders().entrySet() )
        {
            builder.header( header.getKey(), header.getValue() );
        }

        if ( result.getEntity() instanceof byte[] )
        {
            builder.entity( result.getEntity() );
        }
        else
        {
            builder.entity( result.getAsString() );
        }

        return builder.build();
    }

    private WebApplicationException illegalMode( final String mode )
    {
        final String validModes = Joiner.on( "," ).join( RenderMode.values() ).toLowerCase();
        return notFound( "Illegal mode [%s]. Should be one of [%s].", mode, validModes );
    }
}
