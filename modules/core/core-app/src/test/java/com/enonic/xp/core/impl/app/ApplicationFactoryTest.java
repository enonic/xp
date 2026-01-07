package com.enonic.xp.core.impl.app;

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
import com.enonic.xp.node.NodeService;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.server.RunModeSupport;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
