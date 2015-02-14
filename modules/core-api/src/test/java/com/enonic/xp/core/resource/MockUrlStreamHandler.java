package com.enonic.xp.core.resource;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

final class MockUrlStreamHandler
    extends URLStreamHandler
{
    private final ResourceUrlRegistry registry;

    public MockUrlStreamHandler( final ResourceUrlRegistry registry )
    {
        this.registry = registry;
    }

    @Override
    protected URLConnection openConnection( final URL url )
        throws IOException
    {
        final ResourceKey key = ResourceKey.from( url.getPath() );
        final URL newUrl = this.registry.getUrl( key );

        if ( newUrl != null )
        {
            return newUrl.openConnection();
        }

        return null;
    }
}
