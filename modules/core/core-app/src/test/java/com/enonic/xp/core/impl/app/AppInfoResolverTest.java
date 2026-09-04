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

import com.enonic.xp.app.ApplicationType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void descriptor_type_static()
        throws Exception
    {
        final ByteSource source = wrapBundle( newBundle( "myBundle", true ).addResource( "enonic.yaml", ByteSource.wrap(
            "kind: \"Application\"\ntype: \"Static\"\n".getBytes( StandardCharsets.UTF_8 ) ).openStream() ) );

        assertEquals( ApplicationType.STATIC, AppInfoResolver.resolve( source ).type );
    }

    @Test
    void descriptor_type_defaults_to_bundle()
        throws Exception
    {
        final ByteSource withDescriptor = wrapBundle( newBundle( "myBundle", true ).addResource( "enonic.yaml", descriptorYaml( "title" ) ) );
        assertEquals( ApplicationType.BUNDLE, AppInfoResolver.resolve( withDescriptor ).type );

        final ByteSource withoutDescriptor = wrapBundle( newBundle( "myBundle", true ) );
        assertEquals( ApplicationType.BUNDLE, AppInfoResolver.resolve( withoutDescriptor ).type );
    }

    @Test
    void has_cms_descriptor_yaml()
        throws Exception
    {
        final ByteSource source = wrapBundle( newBundle( "myBundle", true ).addResource( "cms/cms.yaml", content( "kind: \"CMS\"" ) ) );

        assertTrue( AppInfoResolver.resolve( source ).hasCmsDescriptor );
    }

    @Test
    void has_cms_descriptor_yml()
        throws Exception
    {
        final ByteSource source = wrapBundle( newBundle( "myBundle", true ).addResource( "cms/cms.yml", content( "kind: \"CMS\"" ) ) );

        assertTrue( AppInfoResolver.resolve( source ).hasCmsDescriptor );
    }

    @Test
    void has_cms_descriptor_missing()
        throws Exception
    {
        final ByteSource withoutCms = wrapBundle( newBundle( "myBundle", true ).addResource( "enonic.yaml", descriptorYaml( "title" ) )
                                                      .addResource( "cms/content-types/mytype/mytype.yaml", content( "kind: \"ContentType\"" ) ) );
        assertFalse( AppInfoResolver.resolve( withoutCms ).hasCmsDescriptor );

        // cms.yaml is only recognized below the cms root
        final ByteSource rootCms = wrapBundle( newBundle( "myBundle", true ).addResource( "cms.yaml", content( "kind: \"CMS\"" ) ) );
        assertFalse( AppInfoResolver.resolve( rootCms ).hasCmsDescriptor );
    }

    private static InputStream content( final String value )
        throws IOException
    {
        return ByteSource.wrap( value.getBytes( StandardCharsets.UTF_8 ) ).openStream();
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
