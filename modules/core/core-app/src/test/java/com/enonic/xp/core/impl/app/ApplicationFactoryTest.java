package com.enonic.xp.core.impl.app;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.ops4j.pax.tinybundles.TinyBundle;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.Application;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.node.NodePath;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApplicationFactoryTest
    extends BundleBasedTest
{
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
        final AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        RunModeSupport.set( RunMode.PROD );

        final Application app = new ApplicationFactory( nodeService, appConfig ).create( bundle );
        assertNotNull( app );
        assertNull( app.getConfig() );
    }

    @Test
    void createUrlResolver_prod_without_virtual_apps()
    {
        final Bundle bundle = deploy( "app1", true, false );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( false );
        RunModeSupport.set( RunMode.PROD );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertInstanceOf( BundleApplicationUrlResolver.class, resolver );
    }

    @Test
    void createUrlResolver_dev_with_source()
    {
        final Bundle bundle = deploy( "app1", true, true );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( true );
        RunModeSupport.set( RunMode.DEV );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertInstanceOf( MultiApplicationUrlResolver.class, resolver );
    }

    @Test
    void createUrlResolver_dev_virtual_not_override()
    {
        final Bundle bundle = deploy( "app1", true, true );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( false );
        RunModeSupport.set( RunMode.DEV );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
    }

    @Test
    void createUrlResolver_prod_virtual_not_override()
    {
        final Bundle bundle = deploy( "app1", true, true );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( false );
        RunModeSupport.set( RunMode.PROD );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
    }

    @Test
    void createUrlResolver_dev_with_source_without_virtual_apps()
    {
        final Bundle bundle = deploy( "app1", true, true );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( false );
        RunModeSupport.set( RunMode.DEV );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertInstanceOf( MultiApplicationUrlResolver.class, resolver );
    }

    @Test
    void createUrlResolver_dev_no_source()
    {
        final Bundle bundle = deploy( "app1", true, false );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( true );
        RunModeSupport.set( RunMode.DEV );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertInstanceOf( MultiApplicationUrlResolver.class, resolver );
    }

    @Test
    void createUrlResolver_dev_no_source_no_virtual_apps()
    {
        final Bundle bundle = deploy( "app1", true, false );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( false );
        RunModeSupport.set( RunMode.DEV );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertInstanceOf( BundleApplicationUrlResolver.class, resolver );
    }

    @Test
    void createUrlResolverByName()
    {
        final Bundle bundle = deploy( "app1", true, true );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( false );
        RunModeSupport.set( RunMode.DEV );

        final ApplicationFactory applicationFactory = new ApplicationFactory( nodeService, appConfig );
        assertInstanceOf( NodeResourceApplicationUrlResolver.class, applicationFactory.createUrlResolver( bundle, "virtual" ) );
        assertInstanceOf( MultiApplicationUrlResolver.class, applicationFactory.createUrlResolver( bundle, "bundle" ) );

        assertThrows( IllegalArgumentException.class, () -> applicationFactory.createUrlResolver( bundle, "unknown" ) );
    }

    @Test
    void static_app_resolver_is_multi_regardless_of_virtual_flags()
    {
        final Bundle bundle = deploy( "app1", createStaticBundle( "app1" ) );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( false );
        RunModeSupport.set( RunMode.PROD );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );
        assertInstanceOf( MultiApplicationUrlResolver.class, resolver );
    }

    @Test
    void static_schema_from_bundle_when_cms_node_missing()
    {
        final Bundle bundle = deploy( "app1", createStaticBundle( "app1" ) );

        final AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( nodeService.nodeExists( any( NodePath.class ) ) ).thenReturn( false );
        when( nodeService.list( any() ) ).thenAnswer( invocation -> Stream.empty() );
        RunModeSupport.set( RunMode.PROD );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );

        assertNotNull( resolver.findResource( "/" + CONTENT_TYPE_PATH ) );
        assertNotNull( resolver.findResource( "/" + ICON_PATH ) );
        assertNotNull( resolver.findResource( "/" + PHRASES_PATH ) );

        final Set<String> files = resolver.findFiles();
        assertTrue( files.contains( CONTENT_TYPE_PATH ) );
        assertTrue( files.contains( ICON_PATH ) );
        assertTrue( files.contains( PHRASES_PATH ) );
    }

    @Test
    void static_schema_not_contributed_by_bundle_when_cms_node_exists()
    {
        final Bundle bundle = deploy( "app1", createStaticBundle( "app1" ) );

        final AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( nodeService.nodeExists( new NodePath( "/applications/app1/cms" ) ) ).thenReturn( true );
        when( nodeService.list( any() ) ).thenAnswer( invocation -> Stream.empty() );
        RunModeSupport.set( RunMode.PROD );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );

        // schema resources, icons included, are served from nodes only
        assertNull( resolver.findResource( "/" + CONTENT_TYPE_PATH ) );
        assertNull( resolver.findResource( "/" + PHRASES_PATH ) );
        assertNull( resolver.findResource( "/" + ICON_PATH ) );

        final Set<String> files = resolver.findFiles();
        assertFalse( files.contains( CONTENT_TYPE_PATH ) );
        assertFalse( files.contains( PHRASES_PATH ) );
        assertFalse( files.contains( ICON_PATH ) );
    }

    @Test
    void bundle_app_without_schema_uses_bundle_resolver()
    {
        // no cms/cms.yaml in the bundle and no persisted schema: plain bundle resolver, schema descriptors come from the bundle
        final Bundle bundle = deploy( "app1", createBundleWithCmsResources( newBundle( "app1", true ) ) );

        final AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( nodeService.nodeExists( any( NodePath.class ) ) ).thenReturn( false );
        RunModeSupport.set( RunMode.PROD );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );

        assertInstanceOf( BundleApplicationUrlResolver.class, resolver );
        assertNotNull( resolver.findResource( "/" + CONTENT_TYPE_PATH ) );
    }

    @Test
    void bundle_app_with_persisted_schema_is_node_backed()
    {
        // no cms/cms.yaml in the bundle, but a schema persisted by an earlier version exists: schema from nodes, logic from the bundle
        final Bundle bundle = deploy( "app1", createBundleWithCmsResources( newBundle( "app1", true ) ).addResource( CONTROLLER_PATH,
                                                                                                                       stream( "controller" ) ) );

        final AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( nodeService.nodeExists( new NodePath( "/applications/app1/cms" ) ) ).thenReturn( true );
        when( nodeService.list( any() ) ).thenAnswer( invocation -> Stream.empty() );
        RunModeSupport.set( RunMode.PROD );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );

        assertInstanceOf( MultiApplicationUrlResolver.class, resolver );
        assertNull( resolver.findResource( "/" + CONTENT_TYPE_PATH ) );
        assertNull( resolver.findResource( "/" + ICON_PATH ) );
        assertNull( resolver.findResource( "/" + PHRASES_PATH ) );
        assertNotNull( resolver.findResource( "/" + CONTROLLER_PATH ) );

        final Set<String> files = resolver.findFiles();
        assertFalse( files.contains( CONTENT_TYPE_PATH ) );
        assertTrue( files.contains( CONTROLLER_PATH ) );
    }

    @Test
    void bundle_app_with_cms_descriptor_is_node_backed()
    {
        // cms/cms.yaml in the bundle: node backed regardless of type; the bundle serves the schema until it is persisted
        final Bundle bundle = deploy( "app1", createBundleWithCmsResources( newBundle( "app1", true ) ).addResource( "cms/cms.yaml", stream(
            "kind: \"CMS\"" ) ) );

        final AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( nodeService.nodeExists( any( NodePath.class ) ) ).thenReturn( false );
        when( nodeService.list( any() ) ).thenAnswer( invocation -> Stream.empty() );
        RunModeSupport.set( RunMode.PROD );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );

        assertInstanceOf( MultiApplicationUrlResolver.class, resolver );
        assertNotNull( resolver.findResource( "/" + CONTENT_TYPE_PATH ) );
        assertNotNull( resolver.findResource( "/cms/cms.yaml" ) );
    }

    @Test
    void local_app_with_cms_descriptor_ignores_persisted_schema()
    {
        // a local application owning its schema is never shadowed by the schema persisted for the global installation
        final Bundle bundle = deploy( "local:app1", createBundleWithCmsResources( newBundle( "app1", true ) ).addResource( "cms/cms.yaml", stream(
            "kind: \"CMS\"" ) ) );

        final AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( nodeService.nodeExists( any( NodePath.class ) ) ).thenReturn( true );
        RunModeSupport.set( RunMode.PROD );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );

        assertInstanceOf( BundleApplicationUrlResolver.class, resolver );
        assertNotNull( resolver.findResource( "/cms/cms.yaml" ) );
        assertNotNull( resolver.findResource( "/" + CONTENT_TYPE_PATH ) );
        assertTrue( resolver.findFiles().contains( CONTENT_TYPE_PATH ) );
    }

    @Test
    void local_app_without_cms_descriptor_uses_persisted_schema()
    {
        // a local application shipping logic only still runs on the persisted schema
        final Bundle bundle = deploy( "local:app1", newBundle( "app1", true ).addResource( CONTROLLER_PATH, stream( "controller" ) ) );

        final AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( nodeService.nodeExists( new NodePath( "/applications/app1/cms" ) ) ).thenReturn( true );
        when( nodeService.list( any() ) ).thenAnswer( invocation -> Stream.empty() );
        RunModeSupport.set( RunMode.PROD );

        final ApplicationUrlResolver resolver = new ApplicationFactory( nodeService, appConfig ).createUrlResolver( bundle, null );

        assertInstanceOf( MultiApplicationUrlResolver.class, resolver );
        assertNotNull( resolver.findResource( "/" + CONTROLLER_PATH ) );
    }

    private static final String CONTENT_TYPE_PATH = "cms/content-types/mytype/mytype.yaml";

    private static final String CONTROLLER_PATH = "cms/parts/mypart/mypart.js";

    private static final String ICON_PATH = "cms/content-types/mytype/mytype.svg";

    private static final String PHRASES_PATH = "cms/i18n/phrases/phrases_en.properties";

    private TinyBundle createStaticBundle( final String name )
    {
        final TinyBundle bundle = newBundle( name, true );
        bundle.addResource( "enonic.yaml", stream( "kind: \"Application\"\ntype: \"Static\"\n" ) );
        return createBundleWithCmsResources( bundle );
    }

    private TinyBundle createBundleWithCmsResources( final TinyBundle bundle )
    {
        bundle.addResource( CONTENT_TYPE_PATH, stream( "kind: \"ContentType\"" ) );
        bundle.addResource( ICON_PATH, stream( "<svg/>" ) );
        bundle.addResource( PHRASES_PATH, stream( "key=value" ) );
        return bundle;
    }

    private static InputStream stream( final String content )
    {
        return new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
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
