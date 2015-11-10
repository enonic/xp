package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;

import com.google.common.collect.Lists;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationEvent;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;

import static org.junit.Assert.*;

public class ApplicationRegistryImplTest
{
    private ApplicationRegistryImpl registry;

    private List<Event> events;

    private BundleContext bundleContext;

    @Before
    public void setup()
        throws Exception
    {
        this.events = Lists.newArrayList();
        this.bundleContext = Mockito.mock( BundleContext.class );
    }

    private void startBundles( final Bundle... bundles )
        throws Exception
    {
        for ( final Bundle bundle : bundles )
        {
            Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        }

        Mockito.when( this.bundleContext.getBundles() ).thenReturn( bundles );
    }

    private void startRegistry()
    {
        this.registry = new ApplicationRegistryImpl();
        this.registry.setEventPublisher( this.events::add );
        this.registry.start( this.bundleContext );
    }

    @Test
    public void testStart_noApplications()
        throws Exception
    {
        startBundles();
        startRegistry();

        final Application application = this.registry.get( ApplicationKey.from( "bundle1" ) );
        assertNull( application );

        final Collection<Application> result = this.registry.getAll();
        assertNotNull( result );
        assertEquals( 0, result.size() );
        assertEquals( 0, this.events.size() );
    }

    private void assertEvent( final int index, final String type, final ApplicationKey key )
    {
        final ApplicationEvent event = (ApplicationEvent) this.events.get( index );
        assertEquals( type, event.getState() );
        assertEquals( key, event.getKey() );
    }

    @Test
    public void testApplicationInstalled()
        throws Exception
    {
        startBundles( newBundle( "bundle1", "Bundle 1" ), newBundle( "bundle2", "Bundle 2" ), newBundle( "bundle3", "Bundle 3" ) );
        startRegistry();

        assertEquals( 2, this.registry.getAll().size() );
        assertEquals( 2, this.events.size() );
        assertEvent( 0, ApplicationEvent.STARTED, ApplicationKey.from( "bundle1" ) );
        assertEvent( 1, ApplicationEvent.STARTED, ApplicationKey.from( "bundle3" ) );

        final Application application1 = this.registry.get( ApplicationKey.from( "bundle1" ) );
        assertNotNull( application1 );
        assertEquals( "Bundle 1", application1.getDisplayName() );

        final Application application2 = this.registry.get( ApplicationKey.from( "bundle3" ) );
        assertNotNull( application2 );
        assertEquals( "Bundle 3", application2.getDisplayName() );
    }

    @Test
    public void testApplicationLifecycle()
        throws Exception
    {
        startBundles( newBundle( "bundle1", "Bundle 1" ) );
        startRegistry();

        final Application application = this.registry.get( ApplicationKey.from( "bundle1" ) );
        assertNotNull( application );
        assertEquals( 1, this.registry.getAll().size() );
        assertEquals( 1, this.events.size() );
        assertEvent( 0, ApplicationEvent.STARTED, ApplicationKey.from( "bundle1" ) );

        this.registry.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, application.getBundle() ) );
        assertEquals( 0, this.registry.getAll().size() );
        assertNull( this.registry.get( ApplicationKey.from( "bundle1" ) ) );
        assertEquals( 2, this.events.size() );
        assertEvent( 1, ApplicationEvent.UNINSTALLED, ApplicationKey.from( "bundle1" ) );

        this.registry.bundleChanged( new BundleEvent( BundleEvent.INSTALLED, application.getBundle() ) );
        assertEquals( 1, this.registry.getAll().size() );
        assertNotNull( this.registry.get( ApplicationKey.from( "bundle1" ) ) );
        assertEquals( 3, this.events.size() );
        assertEvent( 2, ApplicationEvent.INSTALLED, ApplicationKey.from( "bundle1" ) );
    }

    private Bundle newBundle( final String name, final String displayName )
        throws Exception
    {
        final Hashtable<String, String> headers = new Hashtable<>();
        headers.put( Constants.BUNDLE_SYMBOLICNAME, name );
        headers.put( Constants.BUNDLE_VERSION, "1.0.0" );
        headers.put( Constants.BUNDLE_NAME, displayName );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( name );
        Mockito.when( bundle.getEntry( Mockito.any() ) ).then( i -> doGetResource( name, i ) );
        Mockito.when( bundle.getHeaders() ).thenReturn( headers );
        return bundle;
    }

    private URL doGetResource( final String name, final InvocationOnMock invocation )
        throws Exception
    {
        return getClass().getClassLoader().getResource( "bundles/" + name + "/" + invocation.getArguments()[0].toString() );
    }
}
