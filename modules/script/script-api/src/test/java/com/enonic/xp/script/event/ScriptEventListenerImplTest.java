package com.enonic.xp.script.event;

import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.script.serializer.MapSerializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScriptEventListenerImplTest
{
    private Object event;

    private ScriptEventListener listener;

    @BeforeEach
    public void setup()
    {
        this.event = null;
        final Consumer<Object> consumer = o -> event = o;

        this.listener = new ScriptEventListenerBuilder().
            typePattern( "app*" ).
            listener( consumer ).
            application( ApplicationKey.from( "foo.bar" ) ).
            localOnly( true ).
            build();
    }

    @Test
    public void testAccessors()
    {
        assertEquals( "foo.bar", this.listener.getApplication().toString() );
    }

    @Test
    public void notLocalOrigin()
    {
        final Event event = Event.create( "application" ).localOrigin( false ).build();
        this.listener.onEvent( event );

        assertNull( this.event );
    }

    @Test
    public void noMatch()
    {
        final Event event = Event.create( "other" ).localOrigin( true ).build();
        this.listener.onEvent( event );

        assertNull( this.event );
    }

    @Test
    public void testEvent()
    {
        final Event event = Event.create( "application" ).localOrigin( true ).value( "a", 1 ).build();
        this.listener.onEvent( event );

        assertNotNull( this.event );
        assertTrue( this.event instanceof MapSerializable );
    }

    @Test
    public void testException()
    {
        final Consumer<Object> consumer = o -> {
            throw new RuntimeException();
        };

        this.listener = new ScriptEventListenerBuilder().
            typePattern( "app*" ).
            listener( consumer ).
            application( ApplicationKey.from( "foo.bar" ) ).
            build();

        final Event event = Event.create( "application" ).localOrigin( true ).build();
        this.listener.onEvent( event );
    }
}
