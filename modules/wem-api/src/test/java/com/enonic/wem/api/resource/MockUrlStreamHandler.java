package com.enonic.wem.api.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

final class MockUrlStreamHandler
    extends URLStreamHandler
{
    private final File dir;

    public MockUrlStreamHandler( final File dir )
    {
        this.dir = dir;
    }

    @Override
    protected URLConnection openConnection( final URL url )
        throws IOException
    {
        final ResourceKey key = ResourceKey.from( url.getPath() );
        final File moduleDir = new File( this.dir, key.getModule().toString() );
        final File file = new File( moduleDir, key.getPath() );
        return file.toURI().toURL().openConnection();
    }
}
