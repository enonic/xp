package com.enonic.xp.core.impl.app;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationEvent;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;

import static org.junit.Assert.*;

public class ApplicationEventDispatcherTest
    extends ApplicationBundleTest
{
    private ApplicationEventDispatcher dispatcher;

    private List<Event> events;

    @Override
    protected void initialize()
        throws Exception
    {
        this.events = Lists.newArrayList();
    }

    private void startDispatcher()
    {
        this.dispatcher = new ApplicationEventDispatcher();
        this.dispatcher.setEventPublisher( this.events::add );
        this.dispatcher.start( this.bundleContext );
    }

    @Test
    public void testStart_noApplications()
        throws Exception
    {
        startBundles();
        startDispatcher();

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
        startDispatcher();

        assertEquals( 2, this.events.size() );
        assertEvent( 0, ApplicationEvent.STARTED, ApplicationKey.from( "bundle1" ) );
        assertEvent( 1, ApplicationEvent.STARTED, ApplicationKey.from( "bundle3" ) );
    }

    @Test
    public void testApplicationLifecycle()
        throws Exception
    {
        startBundles( newBundle( "bundle1", "Bundle 1" ) );
        startDispatcher();

        assertEquals( 1, this.events.size() );
        assertEvent( 0, ApplicationEvent.STARTED, ApplicationKey.from( "bundle1" ) );

        final Bundle bundle = newBundle( "bundle1", "Bundle 1" );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "bundle1" );

        this.dispatcher.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, bundle ) );
        assertEquals( 2, this.events.size() );
        assertEvent( 1, ApplicationEvent.UNINSTALLED, ApplicationKey.from( "bundle1" ) );

        this.dispatcher.bundleChanged( new BundleEvent( BundleEvent.INSTALLED, bundle ) );
        assertEquals( 3, this.events.size() );
        assertEvent( 2, ApplicationEvent.INSTALLED, ApplicationKey.from( "bundle1" ) );
    }
}
