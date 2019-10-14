package com.enonic.xp.core.impl.app.event;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.BundleBasedTest;
import com.enonic.xp.event.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationEventDispatcherTest
    extends BundleBasedTest
{
    private ApplicationEventDispatcher dispatcher;

    private List<Event> events;

    @BeforeEach
    public void initialize()
    {
        this.events = Lists.newArrayList();
    }

    private void startDispatcher()
    {
        this.dispatcher = new ApplicationEventDispatcher();
        this.dispatcher.setEventPublisher( this.events::add );
        this.dispatcher.start( getBundleContext() );
    }

    @Test
    public void testStart_noApplications()
        throws Exception
    {
        startDispatcher();
        assertEquals( 0, this.events.size() );
    }

    private void assertEvent( final int index, final String type, final ApplicationKey key )
    {
        final Event event = this.events.get( index );
        assertEquals( type, event.getValue( ApplicationEvents.EVENT_TYPE_KEY ).get() );
        assertEquals( key.toString(), event.getValue( ApplicationEvents.APPLICATION_KEY_KEY ).get() );
    }

    @Test
    public void testApplicationInstalled()
        throws Exception
    {
        createBundle( "app1", true ).start();
        createBundle( "app2", true ).start();
        createBundle( "app3", false ).start();

        startDispatcher();

        assertEquals( 2, this.events.size() );
        assertEvent( 0, ApplicationEvents.STARTED, ApplicationKey.from( "app1" ) );
        assertEvent( 1, ApplicationEvents.STARTED, ApplicationKey.from( "app2" ) );
    }

    @Test
    public void testApplicationLifecycle()
        throws Exception
    {
        final Bundle bundle = createBundle( "app1", true );
        bundle.start();

        startDispatcher();

        assertEquals( 1, this.events.size() );
        assertEvent( 0, ApplicationEvents.STARTED, ApplicationKey.from( "app1" ) );

        bundle.stop();

        assertEquals( 3, this.events.size() );
        assertEvent( 1, ApplicationEvents.STOPPING, ApplicationKey.from( "app1" ) );
        assertEvent( 2, ApplicationEvents.STOPPED, ApplicationKey.from( "app1" ) );

        bundle.start();

        assertEquals( 5, this.events.size() );
        assertEvent( 3, ApplicationEvents.STARTING, ApplicationKey.from( "app1" ) );
        assertEvent( 4, ApplicationEvents.STARTED, ApplicationKey.from( "app1" ) );
    }

    private Bundle createBundle( final String key, final boolean isApp )
        throws Exception
    {
        final TinyBundle builder = newBundle( key, isApp );
        return deploy( key, builder );
    }
}
