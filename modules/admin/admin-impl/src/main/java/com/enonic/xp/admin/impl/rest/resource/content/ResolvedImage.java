package com.enonic.xp.admin.impl.rest.resource.content;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

final class ResolvedImage
{
    final Object image;

    final String mimeType;

    ResolvedImage( final Object image, final String mimeType )
    {
        this.image = image;
        this.mimeType = mimeType;
    }

    static ResolvedImage unresolved()
    {
        return new ResolvedImage( null, null );
    }

    boolean isOK()
    {
        return image != null && mimeType != null;
    }

    Response toResponse()
    {
        return Response.ok( image, mimeType ).build();
    }

    Response toResponse( final CacheControl cc )
    {
        return Response.ok( image, mimeType ).cacheControl( cc ).build();
    }
}
