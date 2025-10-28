package com.enonic.xp.core.impl.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventMulticasterTest
{
    private EventMulticaster multicaster;

    @BeforeEach
    void setUp()
    {
        this.multicaster = new EventMulticaster();
    }

    @Test
    void testEventWithoutListeners()
    {
        final Event event = Event.create( "test" ).build();
        this.multicaster.publish( event );
    }

    @Test
    void testPublishOneListener()
    {
        final EventListener listener = mock( EventListener.class );
        this.multicaster.add( listener );

        final Event event = Event.create( "test" ).build();
        this.multicaster.publish( event );

        this.multicaster.remove( listener );
        this.multicaster.publish( event );

        verify( listener, times( 1 ) ).onEvent( event );
    }

    @Test
    void testPublishMultipleListeners()
    {
        final EventListener listener1 = mock( EventListener.class );
        this.multicaster.add( listener1 );

        final EventListener listener2 = mock( EventListener.class );
        this.multicaster.add( listener2 );

        final EventListener listener3 = mock( EventListener.class );
        this.multicaster.add( listener3 );

        final Event event = Event.create( "test" ).build();
        this.multicaster.publish( event );

        verify( listener1, times( 1 ) ).onEvent( event );
        verify( listener2, times( 1 ) ).onEvent( event );
        verify( listener3, times( 1 ) ).onEvent( event );
    }

    @Test
    void testPublishExceptionOnListener()
    {
        final EventListener listener1 = mock( EventListener.class );
        this.multicaster.add( listener1 );

        final EventListener listener2 = mock( EventListener.class );
        this.multicaster.add( listener2 );

        final EventListener listener3 = mock( EventListener.class );
        this.multicaster.add( listener3 );

        doThrow( new RuntimeException( "Error" ) ).when( listener2 ).onEvent( Mockito.any() );

        final Event event = Event.create( "test" ).build();
        this.multicaster.publish( event );

        verify( listener1, times( 1 ) ).onEvent( event );
        verify( listener2, times( 1 ) ).onEvent( event );
        verify( listener3, times( 1 ) ).onEvent( event );
    }

    @Test
    void testListenerOrder()
    {
        final EventListener listener1 = mock( EventListener.class );
        when( listener1.getOrder() ).thenReturn( Integer.MIN_VALUE );
        when( listener1.toString() ).thenReturn( "listener1" );

        final EventListener listener2 = mock( EventListener.class );
        when( listener2.getOrder() ).thenReturn( 200 );
        when( listener2.toString() ).thenReturn( "listener2" );

        final EventListener listener3 = mock( EventListener.class );
        when( listener3.getOrder() ).thenReturn( Integer.MAX_VALUE );
        when( listener3.toString() ).thenReturn( "listener3" );

        this.multicaster.add( listener2 );
        this.multicaster.add( listener1 );
        this.multicaster.add( listener3 );

        final Event event = Event.create( "test" ).build();
        this.multicaster.publish( event );

        InOrder inOrder = inOrder( listener1, listener2, listener3 );

        inOrder.verify( listener1, times( 1 ) ).onEvent( event );
        inOrder.verify( listener2, times( 1 ) ).onEvent( event );
        inOrder.verify( listener3, times( 1 ) ).onEvent( event );
    }
}
