package com.enonic.xp.admin.impl.app;

import java.io.InputStream;
import java.net.URL;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import com.google.common.net.MediaType;

import com.enonic.xp.core.util.MediaTypes;

final class ResourceHandler
{
    private static final String INDEX_HTML = "index.html";

    private ResourceLocator resourceLocator;

    public Response handle( final String path )
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

    private Response serveResource( final String path, final InputStream in )
        throws Exception
    {
        final MediaType mediaType = MediaTypes.instance().fromFile( path );
        return Response.ok( in ).type( mediaType.toString() ).build();
    }

    private InputStream findResource( final String path )
        throws Exception
    {
        if ( this.resourceLocator == null )
        {
            return null;
        }

        if ( path.endsWith( "/" ) )
        {
            return findResource( path + INDEX_HTML );
        }

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
