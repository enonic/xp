package com.enonic.xp.script.impl.event;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.script.event.ScriptEventListener;
import com.enonic.xp.script.event.ScriptEventManager;

@Component(immediate = true, service = {ScriptEventManager.class, ApplicationInvalidator.class, EventListener.class})
public final class ScriptEventManagerImpl
    implements ScriptEventManager, ApplicationInvalidator, EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptEventManagerImpl.class );

    private final CopyOnWriteArrayList<ScriptEventListener> listeners;

    public ScriptEventManagerImpl()
    {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void add( final ScriptEventListener listener )
    {
        LOG.debug( "Add Script Event Listener for {}", listener.getApplication() );
        this.listeners.add( listener );
    }

    @Override
    public Iterator<ScriptEventListener> iterator()
    {
        return this.listeners.iterator();
    }

    @Override
    @Deprecated
    public void invalidate( final ApplicationKey key )
    {
        invalidate( key, ApplicationInvalidationLevel.FULL );
    }

    @Override
    public void invalidate( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
        if ( ApplicationInvalidationLevel.FULL == level )
        {
            LOG.debug( "Remove Script Event Listeners for {}", key );
            this.listeners.removeIf( ( listener ) -> key.equals( listener.getApplication() ) );
        }
    }

    @Override
    public void onEvent( final Event event )
    {
        for ( final ScriptEventListener listener : listeners )
        {
            listener.onEvent( event );
        }
    }
}
