package com.enonic.xp.admin.impl.app;

import java.net.URL;

import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

final class ResourceHandler
{
    private final static int MAX_CACHE_AGE = 365 * 24 * 60 * 60; // 1 year

    private ResourceLocator resourceLocator;

    WebResponse handle( final String path )
        throws Exception
    {
        return handle( path, false );
    }

    WebResponse handle( final String path, final boolean caching )
        throws Exception
    {
        final WebResponse.Builder builder = doHandle( path );
        if ( builder == null )
        {
            return WebResponse.create().status( HttpStatus.NOT_FOUND ).build();
        }

        if ( caching && this.resourceLocator.shouldCache() )
        {
            builder.header( "Cache-Control", "public, no-transform, max-age=" + MAX_CACHE_AGE );
        }

        return builder.build();
    }

    private WebResponse.Builder doHandle( final String path )
        throws Exception
    {
        final byte[] bytes = findResource( path );
        if ( bytes != null )
        {
            return serveResource( path, bytes );
        }
        else
        {
            return null;
        }
    }

    private WebResponse.Builder serveResource( final String path, final byte[] bytes )
        throws Exception
    {
        final MediaType mediaType = MediaTypes.instance().fromFile( path );
        return WebResponse.create().status( HttpStatus.OK ).contentType( mediaType ).body( bytes );
    }

    private byte[] findResource( final String path )
        throws Exception
    {
        final String resourcePath = "/web" + ( path.startsWith( "/" ) ? path : ( "/" + path ) );
        final URL url = this.resourceLocator.findResource( resourcePath );

        if ( url == null )
        {
            return null;
        }

        return Resources.toByteArray( url );
    }

    void setResourceLocator( final ResourceLocator resourceLocator )
    {
        this.resourceLocator = resourceLocator;
    }
}
