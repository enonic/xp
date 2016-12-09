package com.enonic.xp.core.impl.app;

import java.io.IOException;

import org.junit.Test;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Constants;

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
        final ByteSource source = wrapBundle( newBundle( "myBundle", true ) );

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
        final ByteSource source = wrapBundle( newBundle( "myBundle", false ) );
        final String appName = ApplicationNameResolver.resolve( source );

        assertNull( appName );
    }


    @Test
    public void has_application_header()
        throws Exception
    {
        final ByteSource source = wrapBundle( createBundleWithHeader( "myBundle", "1.0.0" ) );
        final String appName = ApplicationNameResolver.resolve( source );

        assertEquals( "myBundle", appName );
    }


    private TinyBundle createBundleWithHeader( final String name, final String version )
    {
        return TinyBundles.bundle().
            set( Constants.BUNDLE_SYMBOLICNAME, name ).
            set( Constants.BUNDLE_VERSION, version ).
            set( ApplicationHelper.X_BUNDLE_TYPE, "application" );
    }

    private ByteSource wrapBundle( final TinyBundle bundle )
        throws IOException
    {
        return ByteSource.wrap( ByteStreams.toByteArray( bundle.build() ) );
    }
}