package com.enonic.xp.app;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationUpdatedTest
{
    @Test
    public void getEventType()
    {
        final ApplicationUpdatedEvent event =
            new ApplicationUpdatedEvent( ApplicationKey.from( "myapplication" ), ApplicationEventType.INSTALLED );
        assertEquals( event.getEventType(), ApplicationEventType.INSTALLED );
    }

    @Test
    public void getApplicationKey()
    {
        final ApplicationUpdatedEvent event =
            new ApplicationUpdatedEvent( ApplicationKey.from( "myapplication" ), ApplicationEventType.INSTALLED );
        assertEquals( event.getApplicationKey().toString(), "myapplication" );
    }

    @Test
    public void testToString()
    {
        ApplicationUpdatedEvent event =
            new ApplicationUpdatedEvent( ApplicationKey.from( "myapplication" ), ApplicationEventType.INSTALLED );
        assertEquals( event.toString(), "ApplicationUpdatedEvent{eventType=INSTALLED, applicationKey=myapplication}" );

        event = new ApplicationUpdatedEvent( null, ApplicationEventType.INSTALLED );
        assertEquals( event.toString(), "ApplicationUpdatedEvent{eventType=INSTALLED}" );

        event = new ApplicationUpdatedEvent( ApplicationKey.from( "myapplication" ), null );
        assertEquals( event.toString(), "ApplicationUpdatedEvent{applicationKey=myapplication}" );

        event = new ApplicationUpdatedEvent( null, null );
        assertEquals( event.toString(), "ApplicationUpdatedEvent{}" );
    }
}
