package com.enonic.xp.core.impl.app;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.ops4j.pax.tinybundles.TinyBundles;
import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationNameResolverTest
    extends BundleBasedTest
{
    @Test
    void valid_bundle()
        throws Exception
    {
        final ByteSource source = wrapBundle( newBundle( "myBundle", true ) );

        final String bundleName = ApplicationNameResolver.resolve( source );

        assertEquals( "myBundle", bundleName );
    }

    @Test
    void invalid_bundle()
    {
        final ByteSource source = ByteSource.wrap( "abc".getBytes() );
        assertThrows(IOException.class, () -> {
                final String appName = ApplicationNameResolver.resolve( source );
                assertNull( appName );
        } );

    }

    @Test
    void not_application()
        throws Exception
    {
        final ByteSource source = wrapBundle( newBundle( "myBundle", false ) );
        assertThrows(ApplicationInvalidException.class, () -> {final String appName = ApplicationNameResolver.resolve( source );
            assertNull( appName );});
    }


    @Test
    void has_application_header()
        throws Exception
    {
        final ByteSource source = wrapBundle( createBundleWithHeader( "myBundle", "1.0.0" ) );
        final String appName = ApplicationNameResolver.resolve( source );

        assertEquals( "myBundle", appName );
    }


    private TinyBundle createBundleWithHeader( final String name, final String version )
    {
        return TinyBundles.bundle()
            .setHeader( Constants.BUNDLE_SYMBOLICNAME, name )
            .setHeader( Constants.BUNDLE_VERSION, version )
            .setHeader( ApplicationManifestConstants.X_BUNDLE_TYPE, "application" );
    }

    private ByteSource wrapBundle( final TinyBundle bundle )
        throws IOException
    {
        return ByteSource.wrap( ByteStreams.toByteArray( bundle.build() ) );
    }
}
