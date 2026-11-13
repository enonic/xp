package com.enonic.xp.core.impl.app;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.resource.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationHelperTest
    extends BundleBasedTest
{
    @Test
    void hasCmsDescriptor_yaml()
    {
        final Bundle bundle = deploy( "app1", newBundle( "app1", true ).addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) );

        assertTrue( ApplicationHelper.hasCmsDescriptor( bundle ) );
    }

    @Test
    void hasCmsDescriptor_yml()
    {
        final Bundle bundle = deploy( "app1", newBundle( "app1", true ).addResource( "cms/cms.yml", stream( "kind: \"CMS\"" ) ) );

        assertTrue( ApplicationHelper.hasCmsDescriptor( bundle ) );
    }

    @Test
    void hasCmsDescriptor_missing()
    {
        final Bundle bundle = deploy( "app1", newBundle( "app1", true ).addResource( "cms.yaml", stream( "kind: \"CMS\"" ) )
            .addResource( "cms/content-types/mytype/mytype.yaml", stream( "kind: \"ContentType\"" ) ) );

        assertFalse( ApplicationHelper.hasCmsDescriptor( bundle ) );
    }

    @Test
    void isLocalApplication_by_bundle_location()
    {
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        assertEquals( "app1", ApplicationHelper.toBundleLocation( appKey, false ) );
        assertEquals( "local:app1", ApplicationHelper.toBundleLocation( appKey, true ) );

        final Bundle global = deploy( ApplicationHelper.toBundleLocation( appKey, false ), newBundle( "app1", true ) );
        assertFalse( ApplicationHelper.isLocalApplication( global ) );
        assertEquals( appKey, ApplicationHelper.getApplicationKey( global ) );

        final Bundle local = deploy( ApplicationHelper.toBundleLocation( appKey, true ), newBundle( "app1", true, "1.0.1" ) );
        assertTrue( ApplicationHelper.isLocalApplication( local ) );
        assertEquals( appKey, ApplicationHelper.getApplicationKey( local ) );
    }

    @Test
    void isCodelessApplication_global_cms_without_bnd_headers()
    {
        final Bundle bundle =
            deploy( "app1", buildWithoutBnd( newBundle( "app1", true ).addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) ) );

        assertTrue( ApplicationHelper.isCodelessApplication( bundle ) );
    }

    @Test
    void isCodelessApplication_built_with_bnd_last_modified()
    {
        final Bundle bundle = deploy( "app1", buildWithoutBnd( newBundle( "app1", true ).setHeader( "Bnd-LastModified", "1783360499694" )
                                                                  .addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) ) );

        assertFalse( ApplicationHelper.isCodelessApplication( bundle ) );
    }

    @Test
    void isCodelessApplication_built_with_bnd_tool()
    {
        final Bundle bundle = deploy( "app1", buildWithoutBnd( newBundle( "app1", true ).setHeader( "Tool", "Bnd-7.3.0.202606021345" )
                                                                  .addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) ) );

        assertFalse( ApplicationHelper.isCodelessApplication( bundle ) );
    }

    @Test
    void isCodelessApplication_other_tool()
    {
        final Bundle bundle = deploy( "app1", buildWithoutBnd( newBundle( "app1", true ).setHeader( "Tool", "Enonic CLI" )
                                                                  .addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) ) );

        assertTrue( ApplicationHelper.isCodelessApplication( bundle ) );
    }

    @Test
    void isCodelessApplication_without_cms_descriptor()
    {
        final Bundle bundle = deploy( "app1", buildWithoutBnd( newBundle( "app1", true ) ) );

        assertFalse( ApplicationHelper.isCodelessApplication( bundle ) );
    }

    @Test
    void isCodelessApplication_local()
    {
        final Bundle bundle = deploy( ApplicationHelper.toBundleLocation( ApplicationKey.from( "app1" ), true ),
                                      buildWithoutBnd( newBundle( "app1", true ).addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) ) );

        assertFalse( ApplicationHelper.isCodelessApplication( bundle ) );
    }

    @Test
    void semVerToOsgi()
    {
        assertEquals( "1.2.3", ApplicationHelper.semVerToOsgi( "1.2.3" ).toString() );
        assertEquals( "1.2.3.SNAPSHOT", ApplicationHelper.semVerToOsgi( "1.2.3-SNAPSHOT" ).toString() );
        assertEquals( "1.2.3.rc_1", ApplicationHelper.semVerToOsgi( "1.2.3-rc.1" ).toString() );
        assertEquals( "1.2.3.rc_1_build_5", ApplicationHelper.semVerToOsgi( "1.2.3-rc.1+build.5" ).toString() );
        assertEquals( "1.2.3.build5", ApplicationHelper.semVerToOsgi( "1.2.3+build5" ).toString() );
        assertEquals( Version.emptyVersion, ApplicationHelper.semVerToOsgi( null ) );
        assertEquals( Version.emptyVersion, ApplicationHelper.semVerToOsgi( "" ) );

        for ( final String invalid : new String[]{"1.2.3.SNAPSHOT", "1.2", "1", "01.2.3", "1.2.3-01", "1.2.3-", "v1.2.3", "abc"} )
        {
            final IllegalArgumentException e =
                assertThrows( IllegalArgumentException.class, () -> ApplicationHelper.semVerToOsgi( invalid ) );
            assertEquals( "invalid semantic version \"" + invalid + "\"", e.getMessage() );
        }
    }

    @Test
    void osgiToSemVer()
    {
        assertEquals( "1.2.3", ApplicationHelper.osgiToSemVer( Version.valueOf( "1.2.3" ) ) );
        assertEquals( "1.2.0", ApplicationHelper.osgiToSemVer( Version.valueOf( "1.2" ) ) );
        assertEquals( "1.2.3-SNAPSHOT", ApplicationHelper.osgiToSemVer( Version.valueOf( "1.2.3.SNAPSHOT" ) ) );
        assertEquals( "1.2.3-rc.1", ApplicationHelper.osgiToSemVer( Version.valueOf( "1.2.3.rc_1" ) ) );
        assertEquals( "1.2.3-rc-1", ApplicationHelper.osgiToSemVer( Version.valueOf( "1.2.3.rc-1" ) ) );
        // an empty identifier is no pre-release: the underscores are written as hyphens
        assertEquals( "1.2.3-a--b", ApplicationHelper.osgiToSemVer( Version.valueOf( "1.2.3.a__b" ) ) );
        // a numeric pre-release takes no leading zero: written as a build metadata
        assertEquals( "1.2.3+007", ApplicationHelper.osgiToSemVer( Version.valueOf( "1.2.3.007" ) ) );

        // there and back
        for ( final String version : new String[]{"1.2.3", "1.2.3.SNAPSHOT", "1.2.3.rc_1", "1.2.3.rc-1", "1.2.3.007"} )
        {
            assertEquals( version, ApplicationHelper.semVerToOsgi( ApplicationHelper.osgiToSemVer( Version.valueOf( version ) ) ).toString() );
        }
    }

    @Test
    void toBundleVersion_takes_both_forms()
    {
        assertEquals( "1.2.3.SNAPSHOT", ApplicationHelper.toBundleVersion( "version", "1.2.3-SNAPSHOT" ).toString() );
        assertEquals( "1.2.3.SNAPSHOT", ApplicationHelper.toBundleVersion( "version", "1.2.3.SNAPSHOT" ).toString() );
        assertEquals( "1.2.0", ApplicationHelper.toBundleVersion( "version", "1.2" ).toString() );
        assertEquals( Version.emptyVersion, ApplicationHelper.toBundleVersion( "version", null ) );

        assertEquals( "1.2.3.SNAPSHOT", ApplicationHelper.normalizeVersion( "version", "1.2.3-SNAPSHOT" ) );
        assertEquals( null, ApplicationHelper.normalizeVersion( "version", "" ) );
        assertEquals( "1.2.3-SNAPSHOT", ApplicationHelper.toSemVer( "version", "1.2.3.SNAPSHOT" ) );
        assertEquals( null, ApplicationHelper.toSemVer( "version", null ) );

        final IllegalArgumentException e =
            assertThrows( IllegalArgumentException.class, () -> ApplicationHelper.toBundleVersion( "minSystemVersion", "9.x" ) );
        assertEquals( "Invalid minSystemVersion in application descriptor: invalid version \"9.x\"", e.getMessage() );
    }

    @Test
    void versionMismatch_compares_a_semantic_version_with_a_bundle_version()
    {
        final ApplicationKey key = ApplicationKey.from( "app1" );

        assertEquals( null, ApplicationHelper.versionMismatch( key, "version", "1.2.3", "Bundle-Version", "1.2.3" ) );
        assertEquals( null, ApplicationHelper.versionMismatch( key, "version", "1.2.3-SNAPSHOT", "Bundle-Version", "1.2.3.SNAPSHOT" ) );
        // whatever the build made of the separators of the pre-release
        assertEquals( null, ApplicationHelper.versionMismatch( key, "version", "1.2.3-rc.1", "Bundle-Version", "1.2.3.rc_1" ) );
        assertEquals( null, ApplicationHelper.versionMismatch( key, "version", "1.2.3-rc.1", "Bundle-Version", "1.2.3.rc1" ) );
        // declared by one of them alone
        assertEquals( null, ApplicationHelper.versionMismatch( key, "version", null, "Bundle-Version", "1.2.3" ) );
        assertEquals( null, ApplicationHelper.versionMismatch( key, "version", "1.2.3", "Bundle-Version", "0.0.0" ) );

        assertEquals( "Application [app1] declares version [1.2.3] in its descriptor, but [1.2.3.SNAPSHOT] in its manifest (Bundle-Version)",
                      ApplicationHelper.versionMismatch( key, "version", "1.2.3", "Bundle-Version", "1.2.3.SNAPSHOT" ) );
        assertEquals( "Application [app1] declares version [1.2.4] in its descriptor, but [1.2.3] in its manifest (Bundle-Version)",
                      ApplicationHelper.versionMismatch( key, "version", "1.2.4", "Bundle-Version", "1.2.3" ) );
    }

    @Test
    void checkSystemVersion_descriptor_range()
    {
        final Version system = Version.valueOf( "8.1.0" );

        ApplicationHelper.checkSystemVersion( null, null, system );
        ApplicationHelper.checkSystemVersion( "", "", system );
        ApplicationHelper.checkSystemVersion( "8.1.0", null, system );
        ApplicationHelper.checkSystemVersion( null, "9.0.0", system );
        ApplicationHelper.checkSystemVersion( "8.0.0", "9.0.0", system );

        // the minimum is inclusive, the maximum exclusive
        assertThrows( ApplicationInvalidVersionException.class, () -> ApplicationHelper.checkSystemVersion( "8.2.0", null, system ) );
        assertThrows( ApplicationInvalidVersionException.class, () -> ApplicationHelper.checkSystemVersion( null, "8.1.0", system ) );
        assertThrows( ApplicationInvalidVersionException.class, () -> ApplicationHelper.checkSystemVersion( "7.0.0", "8.0.0", system ) );
    }

    @Test
    void checkSystemVersion_descriptor_invalid_version()
    {
        final Version system = Version.valueOf( "8.1.0" );

        final IllegalArgumentException min =
            assertThrows( IllegalArgumentException.class, () -> ApplicationHelper.checkSystemVersion( "abc", null, system ) );
        assertTrue( min.getMessage().startsWith( "Invalid minSystemVersion in application descriptor" ), min.getMessage() );

        final IllegalArgumentException max =
            assertThrows( IllegalArgumentException.class, () -> ApplicationHelper.checkSystemVersion( null, "9.x", system ) );
        assertTrue( max.getMessage().startsWith( "Invalid maxSystemVersion in application descriptor" ), max.getMessage() );
    }

    @Test
    void checkSystemVersion_reads_the_descriptor_the_resolver_serves()
    {
        final ApplicationKey key = ApplicationKey.from( "app1" );
        final String yaml = "kind: \"Application\"\nminSystemVersion: \"8.0.0\"\nmaxSystemVersion: \"9.0.0\"\n";
        final ApplicationUrlResolver resolver = descriptorResolver( key, "/enonic.yaml", yaml );

        ApplicationHelper.checkDescriptorSystemVersion( key, resolver, Version.valueOf( "8.1.0" ) );
        assertThrows( ApplicationInvalidVersionException.class,
                      () -> ApplicationHelper.checkDescriptorSystemVersion( key, resolver, Version.valueOf( "7.9.0" ) ) );
        assertThrows( ApplicationInvalidVersionException.class,
                      () -> ApplicationHelper.checkDescriptorSystemVersion( key, resolver, Version.valueOf( "9.0.0" ) ) );
    }

    @Test
    void checkSystemVersion_descriptor_without_versions_or_unreadable()
    {
        final ApplicationKey key = ApplicationKey.from( "app1" );
        final Version system = Version.valueOf( "8.1.0" );

        ApplicationHelper.checkDescriptorSystemVersion( key, null, system );
        ApplicationHelper.checkDescriptorSystemVersion( key, Mockito.mock( ApplicationUrlResolver.class ), system );
        ApplicationHelper.checkDescriptorSystemVersion( key, descriptorResolver( key, "/enonic.yaml", "kind: \"Application\"\n" ), system );
        // a descriptor that cannot be parsed declares nothing to check
        ApplicationHelper.checkDescriptorSystemVersion( key, descriptorResolver( key, "/enonic.yaml", "kind: \"Other\"\n" ), system );
    }

    private static ApplicationUrlResolver descriptorResolver( final ApplicationKey key, final String path, final String yaml )
    {
        final Resource resource = Mockito.mock( Resource.class );
        Mockito.when( resource.exists() ).thenReturn( true );
        Mockito.when( resource.readString() ).thenReturn( yaml );
        final ApplicationUrlResolver resolver = Mockito.mock( ApplicationUrlResolver.class );
        Mockito.when( resolver.findResource( path ) ).thenReturn( resource );
        return resolver;
    }

    private static InputStream stream( final String content )
    {
        return new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
    }
}