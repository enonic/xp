package com.enonic.xp.core.impl.app;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.resource.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationImplTest
    extends BundleBasedTest
{
    @Test
    void testApplication()
        throws Exception
    {
        final Bundle bundle = deployBundle();

        final ApplicationUrlResolver urlResolver = Mockito.mock( ApplicationUrlResolver.class );
        final ApplicationImpl application = new ApplicationImpl( bundle, urlResolver, null );

        assertEquals( "myapplication", application.getKey().toString() );
        assertEquals( bundle, application.getBundle() );
        assertTrue( application.getModifiedTime().getEpochSecond() > 0 );
        assertFalse( application.isStarted() );
        assertTrue( ApplicationBundleUtils.isApplication( bundle ) );
        assertNotNull( application.getClassLoader() );
        assertEquals( "1.0.0", application.getVersion().toString() );
        assertEquals( "[1.2.0,2.0.0)", application.getSystemVersion() );
        assertEquals( "2.0.0", application.getMaxSystemVersion() );
        assertEquals( "1.2.0", application.getMinSystemVersion() );

        // an application with a node declares its versions in the persisted descriptor, the manifest is not consulted
        final ApplicationImpl persisted = new ApplicationImpl( bundle, urlResolver, null, true );
        assertEquals( "0.0.0", persisted.getVersion().toString() );
        assertNull( persisted.getSystemVersion() );
        assertNull( persisted.getMaxSystemVersion() );
        assertNull( persisted.getMinSystemVersion() );

        bundle.start();
        assertTrue( application.isStarted() );
    }

    @Test
    void isLocal()
    {
        final ApplicationKey key = ApplicationKey.from( "myapplication" );
        final ApplicationUrlResolver urlResolver = Mockito.mock( ApplicationUrlResolver.class );

        final Bundle global = deploy( ApplicationHelper.toBundleLocation( key, false ), newBundle( "myapplication", true ) );
        assertFalse( new ApplicationImpl( global, urlResolver, null ).isLocal() );

        final Bundle local = deploy( ApplicationHelper.toBundleLocation( key, true ), newBundle( "myapplication", true, "1.0.1" ) );
        assertTrue( new ApplicationImpl( local, urlResolver, null ).isLocal() );
    }

    @Test
    void versions_are_read_from_the_descriptor_of_an_application_with_a_node()
    {
        // the manifest declares 1.0.0 and [1.2.0,2.0.0)
        final Bundle bundle = deployBundle();

        final Resource descriptor = Mockito.mock( Resource.class );
        when( descriptor.exists() ).thenReturn( true );
        when( descriptor.readString() ).thenReturn(
            "kind: \"Application\"\nversion: \"3.4.5\"\nminSystemVersion: \"8.0.0\"\nmaxSystemVersion: \"9.0.0\"\n" );
        final ApplicationUrlResolver urlResolver = Mockito.mock( ApplicationUrlResolver.class );
        when( urlResolver.isStable() ).thenReturn( true );
        when( urlResolver.revision() ).thenReturn( "v1" );
        when( urlResolver.findResource( "/enonic.yaml" ) ).thenReturn( descriptor );

        final ApplicationImpl application = new ApplicationImpl( bundle, urlResolver, null, true );

        assertEquals( "3.4.5", application.getVersion().toString() );
        assertEquals( "[8.0.0,9.0.0)", application.getSystemVersion() );
        assertEquals( "8.0.0", application.getMinSystemVersion() );
        assertEquals( "9.0.0", application.getMaxSystemVersion() );
        // the descriptor is read once for as long as the node stays the same
        verify( descriptor, times( 1 ) ).readString();

        // the node changed: what the descriptor no longer declares is not read from the manifest
        when( urlResolver.revision() ).thenReturn( "v2" );
        when( descriptor.readString() ).thenReturn( "kind: \"Application\"\n" );

        assertEquals( "0.0.0", application.getVersion().toString() );
        assertNull( application.getSystemVersion() );
        assertNull( application.getMinSystemVersion() );
        assertNull( application.getMaxSystemVersion() );
    }

    @Test
    void versions_of_an_application_without_a_node_are_read_from_the_manifest()
    {
        final ApplicationKey key = ApplicationKey.from( "myapplication" );
        final ApplicationUrlResolver urlResolver = Mockito.mock( ApplicationUrlResolver.class );

        final Bundle local = deploy( ApplicationHelper.toBundleLocation( key, true ), newBundle( "myapplication", true, "1.0.1" ).setHeader(
            ApplicationManifestConstants.X_SYSTEM_VERSION, "[1.2,2)" ) );
        final ApplicationImpl application = new ApplicationImpl( local, urlResolver, null, false );

        assertEquals( "1.0.1", application.getVersion().toString() );
        assertEquals( "[1.2.0,2.0.0)", application.getSystemVersion() );
        assertEquals( "1.2.0", application.getMinSystemVersion() );
        assertEquals( "2.0.0", application.getMaxSystemVersion() );
        verify( urlResolver, never() ).findResource( any() );
    }

    private Bundle deployBundle()
    {
        final InputStream in = newBundle( "myapplication", true )
            .setHeader( ApplicationManifestConstants.X_SYSTEM_VERSION, "[1.2,2)" )
            .setHeader( ApplicationManifestConstants.X_SOURCE_PATHS, "/a/b,/c/d" )
            .build();

        return deploy( "bundle", in );
    }
}
