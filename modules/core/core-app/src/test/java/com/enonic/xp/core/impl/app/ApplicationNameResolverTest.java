package com.enonic.xp.core.impl.app;

import java.io.IOException;

import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import static org.junit.Assert.*;

public class ApplicationNameResolverTest
    extends BundleBasedTest
{
    @Test
    public void valid_bundle()
        throws Exception
    {
        final ByteSource source = createBundle( "myBundle", true );

        final String bundleName = ApplicationNameResolver.resolve( source );

        assertEquals( "myBundle", bundleName );
    }

    @Test(expected = IOException.class)
    public void invalid_bundle()
        throws Exception
    {
        final ByteSource source = ByteSource.wrap( "abc".getBytes() );
        final String appName = ApplicationNameResolver.resolve( source );

        assertNull( appName );
    }

    @Test(expected = ApplicationInvalidException.class)
    public void not_application()
        throws Exception
    {
        final ByteSource source = createBundle( "myBundle", false );
        final String appName = ApplicationNameResolver.resolve( source );

        assertNull( appName );
    }

    private ByteSource createBundle( final String bundleName, final boolean isApp )
        throws IOException
    {
        return ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, isApp ).build() ) );
    }
}