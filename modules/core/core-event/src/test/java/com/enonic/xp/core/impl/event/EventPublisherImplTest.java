package com.enonic.xp.core.impl.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventPublisherImplTest
{
    private EventPublisherImpl publisher;

    @Before
    public void setUp()
    {
        this.publisher = new EventPublisherImpl();
    }

    @Test
    public void testPublish_noListener()
    {
        final Event event = new TestEvent();
        this.publisher.publish( event );
    }

    @Test
    public void testPublish_withListener()
        throws Exception
    {
        final EventListener listener = mock( EventListener.class );
        this.publisher.addListener( listener );

        final Event event = new TestEvent();
        for ( int i = 0; i < 100; i++ )
        {
            this.publisher.publish( event );
        }

        Thread.sleep( 200L );

        verify( listener, times( 100 ) ).onEvent( Mockito.any() );
    }
}
