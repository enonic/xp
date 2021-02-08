package com.enonic.xp.admin.impl.rest.resource.content;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

public final class ResolvedImage
{
    final Object image;

    final String mimeType;

    final boolean gzip;

    public ResolvedImage( final Object image, final String mimeType )
    {
        this.image = image;
        this.mimeType = mimeType;
        this.gzip = false;
    }

    public ResolvedImage( final Object image, final String mimeType, final boolean gzip )
    {
        this.image = image;
        this.mimeType = mimeType;
        this.gzip = gzip;
    }

    public static ResolvedImage unresolved()
    {
        return new ResolvedImage( null, null );
    }

    public boolean isOK()
    {
        return image != null && mimeType != null;
    }

    private Response.ResponseBuilder newResponse()
    {
        final Response.ResponseBuilder r = Response.ok( image, mimeType );
        if ( gzip )
        {
            r.encoding( "gzip" );
        }
        return r;
    }

    public Response toResponse()
    {
        return newResponse().build();
    }

    public Response toResponse( final CacheControl cc )
    {
        return newResponse().cacheControl( cc ).build();
    }
}
