package com.enonic.xp.core.impl.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EventPublisherImplTest
{
    private EventPublisherImpl publisher;

    @BeforeEach
    void setUp()
    {
        this.publisher = new EventPublisherImpl( Runnable::run );
    }

    @Test
    void testPublish_noListener()
    {
        final Event event = Event.create( "test" ).build();
        this.publisher.publish( event );
    }

    @Test
    void testPublish_withListener()
    {
        final EventListener listener = mock( EventListener.class );
        this.publisher.addListener( listener );

        final Event event = Event.create( "test" ).build();
        for ( int i = 0; i < 100; i++ )
        {
            this.publisher.publish( event );
        }

        verify( listener, times( 100 ) ).onEvent( Mockito.any() );
    }
}
