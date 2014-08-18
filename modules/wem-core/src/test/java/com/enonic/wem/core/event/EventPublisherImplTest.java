package com.enonic.wem.core.event;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.event.Event;
import com.enonic.wem.api.event.EventListener;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventPublisherImplTest
{
    private EventPublisherImpl eventPublisher;

    @Before
    public final void setUp()
        throws Exception
    {
        eventPublisher = new EventPublisherImpl();
    }

    @Test
    public void testPublishWithoutListeners()
        throws Exception
    {
        final Event event = new TestEvent();
        eventPublisher.publish( event );
    }

    @Test
    public void testPublishOneListener()
        throws Exception
    {
        final EventListener eventListener1 = mock( EventListener.class );
        final List<EventListener> eventListeners = newArrayList( eventListener1 );
        eventPublisher.setEventListeners( eventListeners );

        final Event event = new TestEvent();
        eventPublisher.publish( event );

        verify( eventListener1, times( 1 ) ).onEvent( any( TestEvent.class ) );
    }

    @Test
    public void testPublishMultipleListeners()
        throws Exception
    {
        final EventListener eventListener1 = mock( EventListener.class );
        final EventListener eventListener2 = mock( EventListener.class );
        final EventListener eventListener3 = mock( EventListener.class );
        final List<EventListener> eventListeners = newArrayList( eventListener1, eventListener2, eventListener3 );
        eventPublisher.setEventListeners( eventListeners );

        final Event event = new TestEvent();
        eventPublisher.publish( event );

        verify( eventListener1, times( 1 ) ).onEvent( any( TestEvent.class ) );
        verify( eventListener2, times( 1 ) ).onEvent( any( TestEvent.class ) );
        verify( eventListener3, times( 1 ) ).onEvent( any( TestEvent.class ) );
    }

    @Test
    public void testPublishExceptionOnListener()
        throws Exception
    {
        final EventListener eventListener1 = mock( EventListener.class );
        final EventListener eventListener2 = mock( EventListener.class );
        final EventListener eventListener3 = mock( EventListener.class );
        final List<EventListener> eventListeners = newArrayList( eventListener1, eventListener2, eventListener3 );
        eventPublisher.setEventListeners( eventListeners );

        doThrow( new RuntimeException( "Error" ) ).when( eventListener2 ).onEvent( any( Event.class ) );

        final Event event = new TestEvent();
        eventPublisher.publish( event );

        verify( eventListener1, times( 1 ) ).onEvent( any( TestEvent.class ) );
        verify( eventListener2, times( 1 ) ).onEvent( any( TestEvent.class ) );
        verify( eventListener3, times( 1 ) ).onEvent( any( TestEvent.class ) );
    }

    class TestEvent
        implements Event
    {
    }
}