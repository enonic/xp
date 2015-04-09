package com.enonic.xp.module;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModuleUpdatedTest
{
    @Test
    public void getEventType()
    {
        final ModuleUpdatedEvent event = new ModuleUpdatedEvent( ModuleKey.from( "mymodule" ), ModuleEventType.INSTALLED );
        assertEquals( event.getEventType(), ModuleEventType.INSTALLED );
    }

    @Test
    public void getModuleKey()
    {
        final ModuleUpdatedEvent event = new ModuleUpdatedEvent( ModuleKey.from( "mymodule" ), ModuleEventType.INSTALLED );
        assertEquals( event.getModuleKey().toString(), "mymodule" );
    }

    @Test
    public void testToString()
    {
        ModuleUpdatedEvent event = new ModuleUpdatedEvent( ModuleKey.from( "mymodule" ), ModuleEventType.INSTALLED );
        assertEquals( event.toString(), "ModuleUpdatedEvent{eventType=INSTALLED, moduleKey=mymodule}" );

        event = new ModuleUpdatedEvent( null, ModuleEventType.INSTALLED );
        assertEquals( event.toString(), "ModuleUpdatedEvent{eventType=INSTALLED}" );

        event = new ModuleUpdatedEvent( ModuleKey.from( "mymodule" ), null );
        assertEquals( event.toString(), "ModuleUpdatedEvent{moduleKey=mymodule}" );

        event = new ModuleUpdatedEvent( null, null );
        assertEquals( event.toString(), "ModuleUpdatedEvent{}" );
    }
}
