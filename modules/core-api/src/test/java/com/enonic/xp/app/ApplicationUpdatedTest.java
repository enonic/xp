package com.enonic.xp.app;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationUpdatedTest
{
    @Test
    public void getEventType()
    {
        final ApplicationUpdatedEvent event = new ApplicationUpdatedEvent( ApplicationKey.from( "mymodule" ), ApplicationEventType.INSTALLED );
        assertEquals( event.getEventType(), ApplicationEventType.INSTALLED );
    }

    @Test
    public void getApplicationKey()
    {
        final ApplicationUpdatedEvent event = new ApplicationUpdatedEvent( ApplicationKey.from( "mymodule" ), ApplicationEventType.INSTALLED );
        assertEquals( event.getApplicationKey().toString(), "mymodule" );
    }

    @Test
    public void testToString()
    {
        ApplicationUpdatedEvent event = new ApplicationUpdatedEvent( ApplicationKey.from( "mymodule" ), ApplicationEventType.INSTALLED );
        assertEquals( event.toString(), "ApplicationUpdatedEvent{eventType=INSTALLED, applicationKey=mymodule}" );

        event = new ApplicationUpdatedEvent( null, ApplicationEventType.INSTALLED );
        assertEquals( event.toString(), "ApplicationUpdatedEvent{eventType=INSTALLED}" );

        event = new ApplicationUpdatedEvent( ApplicationKey.from( "mymodule" ), null );
        assertEquals( event.toString(), "ApplicationUpdatedEvent{applicationKey=mymodule}" );

        event = new ApplicationUpdatedEvent( null, null );
        assertEquals( event.toString(), "ApplicationUpdatedEvent{}" );
    }
}
