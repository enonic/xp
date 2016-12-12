package com.enonic.xp.lib.event;

import java.util.Iterator;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.script.event.ScriptEventListener;
import com.enonic.xp.script.event.ScriptEventManager;
import com.enonic.xp.testing.script.ScriptRunnerSupport;

public class EventScriptTest
    extends ScriptRunnerSupport
{
    private ScriptEventListener listener;

    private Event published;

    private ScriptEventManager manager = new ScriptEventManager()
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

    private EventPublisher publisher = new EventPublisher()
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

    public Event getPublishedEvent()
    {
        return this.published;
    }

    public void fireEvent()
    {
        this.listener.onEvent( Event.create( "application" ).localOrigin( true ).value( "a", 1 ).build() );
    }
}
