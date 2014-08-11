package com.enonic.wem.api.resource;

import java.io.File;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

final class MockUrlStreamHandlerFactory
    implements URLStreamHandlerFactory
{
    private final File dir;

    public MockUrlStreamHandlerFactory( final File dir )
    {
        this.dir = dir;
    }

    @Override
    public URLStreamHandler createURLStreamHandler( final String protocol )
    {
        if ( "module".equals( protocol ) )
        {
            return new MockUrlStreamHandler( dir );
        }

        return null;
    }
}
