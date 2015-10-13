package com.enonic.xp.core.impl.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventMulticasterTest
{
    private EventMulticaster multicaster;

    @Before
    public void setUp()
    {
        this.multicaster = new EventMulticaster();
    }

    @Test
    public void testEventWithoutListeners()
    {
        final Event event = new TestEvent();
        this.multicaster.publish( event );
    }

    @Test
    public void testPublishOneListener()
    {
        final EventListener listener = mock( EventListener.class );
        this.multicaster.add( listener );

        final Event event = new TestEvent();
        this.multicaster.publish( event );

        this.multicaster.remove( listener );
        this.multicaster.publish( event );

        verify( listener, times( 1 ) ).onEvent( event );
    }

    @Test
    public void testPublishMultipleListeners()
    {
        final EventListener listener1 = mock( EventListener.class );
        this.multicaster.add( listener1 );

        final EventListener listener2 = mock( EventListener.class );
        this.multicaster.add( listener2 );

        final EventListener listener3 = mock( EventListener.class );
        this.multicaster.add( listener3 );

        final Event event = new TestEvent();
        this.multicaster.publish( event );

        verify( listener1, times( 1 ) ).onEvent( event );
        verify( listener2, times( 1 ) ).onEvent( event );
        verify( listener3, times( 1 ) ).onEvent( event );
    }

    @Test
    public void testPublishExceptionOnListener()
    {
        final EventListener listener1 = mock( EventListener.class );
        this.multicaster.add( listener1 );

        final EventListener listener2 = mock( EventListener.class );
        this.multicaster.add( listener2 );

        final EventListener listener3 = mock( EventListener.class );
        this.multicaster.add( listener3 );

        doThrow( new RuntimeException( "Error" ) ).when( listener2 ).onEvent( Mockito.any() );

        final Event event = new TestEvent();
        this.multicaster.publish( event );

        verify( listener1, times( 1 ) ).onEvent( event );
        verify( listener2, times( 1 ) ).onEvent( event );
        verify( listener3, times( 1 ) ).onEvent( event );
    }
}
