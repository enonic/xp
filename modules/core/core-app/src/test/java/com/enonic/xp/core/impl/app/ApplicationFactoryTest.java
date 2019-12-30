package com.enonic.xp.core.impl.app;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;

import com.enonic.xp.app.Application;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.internal.Dictionaries;
import com.enonic.xp.server.RunMode;

import static com.enonic.xp.core.impl.app.ApplicationHelper.X_BUNDLE_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class ApplicationFactoryTest
    extends BundleBasedTest
{
    @Test
    public void create_app()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, false );

        final Application app = new ApplicationFactory( RunMode.PROD ).create( bundle );
        assertNotNull( app );
        assertNotNull( app.getConfig() );
    }

    @Test
    public void create_notApp()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", false, false );

        final Application app = new ApplicationFactory( RunMode.PROD ).create( bundle );
        assertNull( app );
    }

    @Test
    public void createUrlResolver_prod()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, false );

        final ApplicationUrlResolver resolver = new ApplicationFactory( RunMode.PROD ).createUrlResolver( bundle );
        assertNotNull( resolver );
        assertTrue( resolver instanceof BundleApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_dev_with_source()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, true );

        final ApplicationUrlResolver resolver = new ApplicationFactory( RunMode.DEV ).createUrlResolver( bundle );
        assertNotNull( resolver );
        assertTrue( resolver instanceof MultiApplicationUrlResolver );
    }

    @Test
    public void createUrlResolver_dev_no_source()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", true, false );

        final ApplicationUrlResolver resolver = new ApplicationFactory( RunMode.DEV ).createUrlResolver( bundle );
        assertNotNull( resolver );
        assertTrue( resolver instanceof BundleApplicationUrlResolver );
    }

    @Test
    public void create_app_config_not_loaded()
        throws Exception
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "app1" );

        Mockito.when( bundle.getHeaders() ).thenReturn( Dictionaries.of( X_BUNDLE_TYPE, "application" ) );

        final BundleContext ctx = Mockito.mock( BundleContext.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( ctx );

        final ServiceReference<ConfigurationAdmin> servRef = Mockito.mock( ServiceReference.class );
        Mockito.when( ctx.getServiceReference( any( Class.class ) ) ).thenReturn( servRef );

        final ConfigurationAdmin configAdmin = Mockito.mock( ConfigurationAdmin.class );
        Mockito.when( ctx.getService( any() ) ).thenReturn( configAdmin );

        final org.osgi.service.cm.Configuration cfg = Mockito.mock( org.osgi.service.cm.Configuration.class );
        Mockito.when( configAdmin.getConfiguration( anyString() ) ).thenReturn( cfg );

        Mockito.when( cfg.getProperties() ).thenReturn( Dictionaries.of( "key", "value" ) );

        final Application app = new ApplicationFactory( RunMode.PROD ).create( bundle );
        assertNotNull( app );
        assertNotNull( app.getConfig() );
        assertEquals( "value", app.getConfig().get( "key" ) );
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
        tinyBundle.set( ApplicationHelper.X_SOURCE_PATHS, "my/source/path" );

        if ( isApp )
        {
            tinyBundle.set( X_BUNDLE_TYPE, "application" );
        }

        return tinyBundle;
    }
}
