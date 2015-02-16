package com.enonic.xp.admin.impl.app;

import java.io.InputStream;
import java.net.URL;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.google.common.net.MediaType;

import com.enonic.xp.util.MediaTypes;

final class ResourceHandler
{
    private final static int MAX_CACHE_AGE = 365 * 24 * 60 * 60; // 1 year

    private ResourceLocator resourceLocator;

    public Response handle( final String path )
        throws Exception
    {
        return handle( path, false );
    }

    public Response handle( final String path, final boolean caching )
        throws Exception
    {
        final Response.ResponseBuilder builder = doHandle( path );

        if ( caching && this.resourceLocator.shouldCache() )
        {
            final CacheControl control = new CacheControl();
            control.setMaxAge( MAX_CACHE_AGE );
            builder.cacheControl( control );
        }

        return builder.build();
    }

    public Response.ResponseBuilder doHandle( final String path )
        throws Exception
    {
        final InputStream in = findResource( path );
        if ( in != null )
        {
            return serveResource( path, in );
        }
        else
        {
            throw new NotFoundException();
        }
    }

    private Response.ResponseBuilder serveResource( final String path, final InputStream in )
        throws Exception
    {
        final MediaType mediaType = MediaTypes.instance().fromFile( path );
        return Response.ok( in ).type( mediaType.toString() );
    }

    private InputStream findResource( final String path )
        throws Exception
    {
        final String resourcePath = "/web" + ( path.startsWith( "/" ) ? path : ( "/" + path ) );
        final URL url = this.resourceLocator.findResource( resourcePath );

        if ( url == null )
        {
            return null;
        }

        return url.openStream();
    }

    public void setResourceLocator( final ResourceLocator resourceLocator )
    {
        this.resourceLocator = resourceLocator;
    }
}
