package com.enonic.xp.itest.core;

import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Lists;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.event.EventPublisher;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EventPublisherTest
    extends AbstractOsgiTest
{
    @Inject
    private EventPublisher eventPublisher;

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public static Option[] config()
    {
        return new Option[]{ //
            CoreOptions.junitBundles(), //
            bundle( "org.apache.felix", "org.apache.felix.configadmin", "1.8.0" ), //
            bundle( "org.apache.felix", "org.apache.felix.scr", "1.8.2" ), //
            bundle( "com.google.guava", "guava", "18.0" ), //
            bundle( "commons-lang", "commons-lang", "2.4" ), //
            bundle( "commons-io", "commons-io", "2.4" ), //
            bundle( "com.fasterxml.jackson.core", "jackson-core", "2.4.1" ), //
            bundle( "com.fasterxml.jackson.core", "jackson-annotations", "2.4.1" ), //
            bundle( "com.fasterxml.jackson.core", "jackson-databind", "2.4.1" ), //
            bundle( "com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.4.1" ), //
            bundle( "com.enonic.osgi.bundles", "jparsec", "2.1.0_1" ), //
            project( "core-api" ), //
            project( "core-event" ) //
        };
    }

    @Test
    public void testPublish()
    {
        final MyEventListener listener = new MyEventListener();
        this.bundleContext.registerService( EventListener.class, listener, null );

        this.eventPublisher.publish( new MyEvent() );
        this.eventPublisher.publish( new MyEvent() );

        Assert.assertEquals( 2, listener.getEvents().size() );
    }

    private final class MyEvent
        implements Event
    {
    }

    private final class MyEventListener
        implements EventListener
    {
        private final List<Event> events;

        public MyEventListener()
        {
            this.events = Lists.newArrayList();
        }

        @Override
        public void onEvent( final Event event )
        {
            this.events.add( event );
        }

        public List<Event> getEvents()
        {
            return this.events;
        }
    }
}
