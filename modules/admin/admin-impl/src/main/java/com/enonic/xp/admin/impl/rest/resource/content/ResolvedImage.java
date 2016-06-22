package com.enonic.xp.admin.impl.rest.resource.content;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

final class ResolvedImage
{
    final Object image;

    final String mimeType;

    final boolean gzip;

    ResolvedImage( final Object image, final String mimeType )
    {
        this.image = image;
        this.mimeType = mimeType;
        this.gzip = false;
    }

    ResolvedImage( final Object image, final String mimeType, final String fileName )
    {
        this.image = image;
        this.mimeType = mimeType;
        this.gzip = fileName != null && fileName.toLowerCase().endsWith( ".svgz" );
    }

    static ResolvedImage unresolved()
    {
        return new ResolvedImage( null, null );
    }

    boolean isOK()
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

    Response toResponse()
    {
        return newResponse().build();
    }

    Response toResponse( final CacheControl cc )
    {
        return newResponse().cacheControl( cc ).build();
    }
}
