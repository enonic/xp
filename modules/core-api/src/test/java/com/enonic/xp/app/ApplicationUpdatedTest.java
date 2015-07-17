package com.enonic.xp.app;

import org.junit.Test;

import com.enonic.xp.module.ModuleUpdatedEvent;

import static org.junit.Assert.*;

public class ApplicationUpdatedTest
{
    @Test
    public void getEventType()
    {
        final ModuleUpdatedEvent event = new ModuleUpdatedEvent( ApplicationKey.from( "mymodule" ), ApplicationEventType.INSTALLED );
        assertEquals( event.getEventType(), ApplicationEventType.INSTALLED );
    }

    @Test
    public void getApplicationKey()
    {
        final ModuleUpdatedEvent event = new ModuleUpdatedEvent( ApplicationKey.from( "mymodule" ), ApplicationEventType.INSTALLED );
        assertEquals( event.getApplicationKey().toString(), "mymodule" );
    }

    @Test
    public void testToString()
    {
        ModuleUpdatedEvent event = new ModuleUpdatedEvent( ApplicationKey.from( "mymodule" ), ApplicationEventType.INSTALLED );
        assertEquals( event.toString(), "ModuleUpdatedEvent{eventType=INSTALLED, applicationKey=mymodule}" );

        event = new ModuleUpdatedEvent( null, ApplicationEventType.INSTALLED );
        assertEquals( event.toString(), "ModuleUpdatedEvent{eventType=INSTALLED}" );

        event = new ModuleUpdatedEvent( ApplicationKey.from( "mymodule" ), null );
        assertEquals( event.toString(), "ModuleUpdatedEvent{applicationKey=mymodule}" );

        event = new ModuleUpdatedEvent( null, null );
        assertEquals( event.toString(), "ModuleUpdatedEvent{}" );
    }
}
