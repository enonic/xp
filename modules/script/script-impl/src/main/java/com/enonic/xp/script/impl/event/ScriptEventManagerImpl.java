package com.enonic.xp.script.impl.event;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.script.event.ScriptEventListener;
import com.enonic.xp.script.event.ScriptEventManager;
import com.enonic.xp.script.impl.async.ScriptAsyncService;

@Component(immediate = true)
public final class ScriptEventManagerImpl
    implements ScriptEventManager, EventListener, ApplicationInvalidator
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptEventManagerImpl.class );

    private final CopyOnWriteArrayList<ScriptEventListenerWrapper> listeners = new CopyOnWriteArrayList<>();

    private final ScriptAsyncService scriptAsyncService;

    @Activate
    public ScriptEventManagerImpl( @Reference final ScriptAsyncService scriptAsyncService )
    {
        this.scriptAsyncService = scriptAsyncService;
    }

    @Deactivate
    public void deactivate()
    {
        listeners.clear();
    }

    @Override
    public void add( final ScriptEventListener listener )
    {
        final ScriptEventListenerWrapper wrapper = new ScriptEventListenerWrapper( listener );
        listeners.add( wrapper );
        LOG.debug( "Added Script Event Listener for {}", wrapper.applicationKey );
    }

    @Override
    public void invalidate( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
        final boolean removed = listeners.removeIf( w -> w.applicationKey.equals( key ) );
        if ( removed )
        {
            LOG.info( "Removed all Script Event Listeners for {}", key );
        }
    }

    @Override
    public Iterator<ScriptEventListener> iterator()
    {
        return listeners.stream().map( w -> w.listener ).iterator();
    }

    @Override
    public void onEvent( final Event event )
    {
        listeners.forEach( listener -> listener.onEvent( event ) );
    }

    private class ScriptEventListenerWrapper
    {
        private final ApplicationKey applicationKey;

        private final ScriptEventListener listener;

        private final Executor asyncExecutor;

        ScriptEventListenerWrapper( final ScriptEventListener listener )
        {
            this.listener = listener;
            this.applicationKey = listener.getApplication();
            this.asyncExecutor = scriptAsyncService.getAsyncExecutor( this.applicationKey );
        }

        public void onEvent( final Event event )
        {
            try
            {
                asyncExecutor.execute( () -> listener.onEvent( event ) );
            }
            catch ( RejectedExecutionException e )
            {
                // Async executor is shutdown as soon as application's bundle is not STARTED anymore.
                // There is still a change for events to arrive so we catch RejectedExecutionException
                // and remove Event Listener as it is not functioning anymore.
                final boolean removed = listeners.remove( this );
                if ( removed )
                {
                    LOG.info( "Removed Script Event Listener for {}", applicationKey );
                }
            }
        }
    }
}
