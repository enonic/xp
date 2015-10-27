package com.enonic.xp.core.impl.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

import static org.junit.Assert.*;
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

    @Test
    public void testListenerOrder()
    {
        final EventListener listener1 = mock( EventListener.class );
        Mockito.when( listener1.getOrder() ).thenReturn( 100 );
        Mockito.when( listener1.toString() ).thenReturn( "listener1" );

        final EventListener listener2 = mock( EventListener.class );
        Mockito.when( listener2.getOrder() ).thenReturn( 200 );
        Mockito.when( listener2.toString() ).thenReturn( "listener2" );

        final EventListener listener3 = mock( EventListener.class );
        Mockito.when( listener3.getOrder() ).thenReturn( Integer.MAX_VALUE );
        Mockito.when( listener3.toString() ).thenReturn( "listener3" );

        this.multicaster.add( listener2 );
        this.multicaster.add( listener1 );
        this.multicaster.add( listener3 );

        assertEquals( 3, this.multicaster.listeners.size() );
        assertSame( listener1, this.multicaster.listeners.get( 0 ) );
        assertSame( listener2, this.multicaster.listeners.get( 1 ) );
        assertSame( listener3, this.multicaster.listeners.get( 2 ) );
    }
}
