package com.enonic.xp.core.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.Application;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.RealOverVirtualApplicationUrlResolver;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.server.RunMode;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplicationFactoryTest
    extends BundleBasedTest
{
    private NodeService nodeService;

    @BeforeEach
    public void init()
    {
        nodeService = Mockito.mock( NodeService.class );
    }

    @Test
    public void create_app()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, false );
        final AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        final Application app = new ApplicationFactory( RunMode.PROD, nodeService, appConfig ).create( bundle );
        assertNotNull( app );
        assertNull( app.getConfig() );
    }

    @Test
    public void createUrlResolver_prod_without_virtual_apps()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, false );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( false );

        final ApplicationUrlResolver resolver =
            new ApplicationFactory( RunMode.PROD, nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertTrue( resolver instanceof BundleApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_dev_with_source()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, true );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( true );

        final ApplicationUrlResolver resolver =
            new ApplicationFactory( RunMode.DEV, nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertTrue( resolver instanceof MultiApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_dev_virtual_not_override()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, true );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( false );

        final ApplicationUrlResolver resolver =
            new ApplicationFactory( RunMode.DEV, nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertTrue( resolver instanceof RealOverVirtualApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_prod_virtual_not_override()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, true );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( false );

        final ApplicationUrlResolver resolver =
            new ApplicationFactory( RunMode.PROD, nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertTrue( resolver instanceof RealOverVirtualApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_dev_with_source_without_virtual_apps()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, true );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( false );

        final ApplicationUrlResolver resolver =
            new ApplicationFactory( RunMode.DEV, nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertTrue( resolver instanceof MultiApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_dev_no_source()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, false );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( true );

        final ApplicationUrlResolver resolver =
            new ApplicationFactory( RunMode.DEV, nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertTrue( resolver instanceof MultiApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_dev_no_source_no_virtual_apps()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, false );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( false );

        final ApplicationUrlResolver resolver =
            new ApplicationFactory( RunMode.DEV, nodeService, appConfig ).createUrlResolver( bundle, null );
        assertNotNull( resolver );
        assertTrue( resolver instanceof BundleApplicationUrlResolver );
    }

    @Test
    public void createUrlResolverByName()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, true );

        final AppConfig appConfig = mock( AppConfig.class );
        when( appConfig.virtual_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( false );

        final ApplicationFactory applicationFactory = new ApplicationFactory( RunMode.DEV, nodeService, appConfig );
        assertTrue( applicationFactory.createUrlResolver( bundle, "virtual" ) instanceof NodeResourceApplicationUrlResolver );
        assertTrue( applicationFactory.createUrlResolver( bundle, "bundle" ) instanceof MultiApplicationUrlResolver );

        assertThrows( IllegalArgumentException.class, () -> applicationFactory.createUrlResolver( bundle, "unknown" ) );
    }

    private Bundle deploy( final String name, final boolean isApp, final boolean hasSourcePath )
        throws Exception
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
        tinyBundle.set( ApplicationManifestConstants.X_SOURCE_PATHS, "my/source/path" );

        if ( isApp )
        {
            tinyBundle.set( ApplicationManifestConstants.X_BUNDLE_TYPE, "application" );
        }

        return tinyBundle;
    }
}
