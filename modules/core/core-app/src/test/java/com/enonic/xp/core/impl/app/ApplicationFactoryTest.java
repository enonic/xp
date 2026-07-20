package com.enonic.xp.core.impl.app;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.Application;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.server.RunModeSupport;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ApplicationFactoryTest
    extends BundleBasedTest
{
    private static final String DESCRIPTOR_PATH = "cms/content-types/mytype/mytype.yaml";

    private static final String ICON_PATH = "cms/content-types/mytype/mytype.svg";

    private NodeService nodeService;

    @BeforeEach
    void init()
    {
        nodeService = Mockito.mock( NodeService.class );
    }

    @Test
    void create_app()
    {
        final Bundle bundle = deploy( "app1", true, false );
        RunModeSupport.set( RunMode.PROD );

        final Application app = new ApplicationFactory( nodeService ).create( bundle );
        assertNotNull( app );
        assertNull( app.getConfig() );
    }

    @Test
    void createUrlResolver_prod()
    {
        final Bundle bundle = deploy( "app1", true, false );

        RunModeSupport.set( RunMode.PROD );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertInstanceOf( MultiApplicationUrlResolver.class, resolver );
    }

    @Test
    void createUrlResolver_dev_with_source_path()
    {
        final Bundle bundle = deploy( "app1", true, true );

        RunModeSupport.set( RunMode.DEV );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertInstanceOf( MultiApplicationUrlResolver.class, resolver );
    }

    @Test
    void createUrlResolverByName()
    {
        final Bundle bundle = deploy( "app1", true, true );

        RunModeSupport.set( RunMode.DEV );

        final ApplicationFactory applicationFactory = new ApplicationFactory( nodeService );
        assertInstanceOf( NodeResourceApplicationUrlResolver.class, applicationFactory.createUrlResolver( bundle, "virtual" ) );
        assertInstanceOf( MultiApplicationUrlResolver.class, applicationFactory.createUrlResolver( bundle, "bundle" ) );

        assertThrows( IllegalArgumentException.class, () -> applicationFactory.createUrlResolver( bundle, "unknown" ) );
    }

    @Test
    void schemaDescriptor_fromBundle_whenAppNodeMissing()
    {
        final Bundle bundle = deploy( "app1", createBundleWithCmsResources() );
        RunModeSupport.set( RunMode.PROD );

        when( nodeService.nodeExists( any( NodePath.class ) ) ).thenReturn( false );
        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create().build() );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService ).createUrlResolver( bundle, null );

        assertNotNull( resolver.findResource( "/" + DESCRIPTOR_PATH ) );
        assertNotNull( resolver.findResource( "/" + ICON_PATH ) );

        final Set<String> files = resolver.findFiles();
        assertTrue( files.contains( DESCRIPTOR_PATH ) );
        assertTrue( files.contains( ICON_PATH ) );
    }

    @Test
    void schemaDescriptor_notContributedByBundle_whenAppNodeExists()
    {
        final Bundle bundle = deploy( "app1", createBundleWithCmsResources() );
        RunModeSupport.set( RunMode.PROD );

        when( nodeService.nodeExists( any( NodePath.class ) ) ).thenReturn( true );
        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create().build() );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService ).createUrlResolver( bundle, null );

        assertNull( resolver.findResource( "/" + DESCRIPTOR_PATH ) );
        assertNotNull( resolver.findResource( "/" + ICON_PATH ) );

        final Set<String> files = resolver.findFiles();
        assertFalse( files.contains( DESCRIPTOR_PATH ) );
        assertTrue( files.contains( ICON_PATH ) );
    }

    private TinyBundle createBundleWithCmsResources()
    {
        return newBundle( "app1", true )
            .addResource( DESCRIPTOR_PATH, new ByteArrayInputStream( "kind: \"ContentType\"".getBytes( StandardCharsets.UTF_8 ) ) )
            .addResource( ICON_PATH, new ByteArrayInputStream( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );
    }

    private Bundle deploy( final String name, final boolean isApp, final boolean hasSourcePath )
    {
        if ( hasSourcePath )
        {
            return deploy( name, createBundleWithSourcePath( name, isApp ) );
        }

        return deploy( name, newBundle( name, isApp ) );
    }

    private TinyBundle createBundleWithSourcePath( final String name, final boolean isApp )
    {
        final TinyBundle tinyBundle = newBundle( name, isApp );
        tinyBundle.setHeader( ApplicationManifestConstants.X_SOURCE_PATHS, "my/source/path" );

        if ( isApp )
        {
            tinyBundle.setHeader( ApplicationManifestConstants.X_BUNDLE_TYPE, "application" );
        }

        return tinyBundle;
    }
}