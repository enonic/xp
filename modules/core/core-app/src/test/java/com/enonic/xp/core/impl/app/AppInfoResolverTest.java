package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.ops4j.pax.tinybundles.TinyBundles;
import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppInfoResolverTest
    extends BundleBasedTest
{
    @Test
    void valid_bundle()
        throws Exception
    {
        final ByteSource source = wrapBundle( newBundle( "myBundle", true ) );

        final String bundleName = AppInfoResolver.resolve( source ).name;

        assertEquals( "myBundle", bundleName );
    }

    @Test
    void invalid_bundle()
    {
        final ByteSource source = ByteSource.wrap( "abc".getBytes() );
        assertThrows(IOException.class, () -> {
                AppInfoResolver.resolve( source );
        } );

    }

    @Test
    void not_application()
        throws Exception
    {
        final ByteSource source = wrapBundle( newBundle( "myBundle", false ) );
        assertThrows(ApplicationInvalidException.class, () -> {AppInfoResolver.resolve( source ); });
    }


    @Test
    void has_application_header()
        throws Exception
    {
        final ByteSource source = wrapBundle( createBundleWithHeader( "myBundle", "1.0.0" ) );
        final String appName = AppInfoResolver.resolve( source ).name;

        assertEquals( "myBundle", appName );
    }

    @Test
    void descriptor_enonic_yaml()
        throws Exception
    {
        final ByteSource source =
            wrapBundle( newBundle( "myBundle", false ).addResource( "enonic.yaml", descriptorYaml( "Enonic title" ) ) );

        final AppInfo appInfo = AppInfoResolver.resolve( source );

        assertEquals( "Enonic title", appInfo.title );
    }

    @Test
    void descriptor_enonic_yaml_preferred_over_application_yaml()
        throws Exception
    {
        final ByteSource source = wrapBundle( newBundle( "myBundle", true ).addResource( "application.yaml",
                                                                                         descriptorYaml( "Application title" ) )
                                                  .addResource( "enonic.yaml", descriptorYaml( "Enonic title" ) ) );

        final AppInfo appInfo = AppInfoResolver.resolve( source );

        assertEquals( "Enonic title", appInfo.title );
    }

    @Test
    void descriptor_application_yml()
        throws Exception
    {
        final ByteSource source =
            wrapBundle( newBundle( "myBundle", true ).addResource( "application.yml", descriptorYaml( "Application title" ) ) );

        final AppInfo appInfo = AppInfoResolver.resolve( source );

        assertEquals( "Application title", appInfo.title );
    }

    private static InputStream descriptorYaml( final String title )
        throws IOException
    {
        return ByteSource.wrap( ( "kind: \"Application\"\ntitle: \"" + title + "\"\n" ).getBytes( StandardCharsets.UTF_8 ) )
            .openStream();
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
