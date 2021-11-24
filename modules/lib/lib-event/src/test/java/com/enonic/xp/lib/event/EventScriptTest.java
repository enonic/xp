package com.enonic.xp.lib.event;

import java.util.Iterator;
import java.util.Map;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.script.event.ScriptEventListener;
import com.enonic.xp.script.event.ScriptEventManager;
import com.enonic.xp.testing.ScriptRunnerSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventScriptTest
    extends ScriptRunnerSupport
{
    private ScriptEventListener listener;

    private Event published;

    private final ScriptEventManager manager = new ScriptEventManager()
    {
        @Override
        public void add( final ScriptEventListener l )
        {
            listener = l;
        }

        @Override
        public Iterator<ScriptEventListener> iterator()
        {
            return null;
        }
    };

    private final EventPublisher publisher = new EventPublisher()
    {
        @Override
        public void publish( final Event event )
        {
            published = event;
        }
    };

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        addService( ScriptEventManager.class, this.manager );
        addService( EventPublisher.class, this.publisher );
    }

    @Override
    public String getScriptTestFile()
    {
        return "/test/event-test.js";
    }

    public void checkPublishedEvent()
    {
        assertEquals( "custom.myEvent", this.published.getType() );
        assertTrue(this.published.isDistributed() );
        assertEquals( Map.of("a", 1, "b", 2), this.published.getData() );
    }

    public void fireEvent()
    {
        this.listener.onEvent( Event.create( "application" ).localOrigin( true ).value( "a", 1 ).build() );
    }
}
