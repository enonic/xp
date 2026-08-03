package com.enonic.xp.script.impl.event;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.script.event.ScriptEventListener;
import com.enonic.xp.script.event.ScriptEventManager;
import com.enonic.xp.script.impl.async.ScriptAsyncService;

// the services this component provides, spelled out: tracking applications is how it maintains
// itself, not something to publish
@Component(immediate = true, service = {ScriptEventManager.class, EventListener.class})
public final class ScriptEventManagerImpl
    implements ScriptEventManager, EventListener, ServiceTrackerCustomizer<Application, Application>
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptEventManagerImpl.class );

    private final CopyOnWriteArrayList<ScriptEventListenerWrapper> listeners = new CopyOnWriteArrayList<>();

    private final ScriptAsyncService scriptAsyncService;

    private final BundleContext context;

    private final ServiceTracker<Application, Application> tracker;

    @Activate
    public ScriptEventManagerImpl( final BundleContext context, @Reference final ScriptAsyncService scriptAsyncService )
    {
        this.context = context;
        this.scriptAsyncService = scriptAsyncService;
        this.tracker = new ServiceTracker<>( context, Application.class, this );
        this.tracker.open();
    }

    @Deactivate
    public void deactivate()
    {
        tracker.close();
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
    public Application addingService( final ServiceReference<Application> reference )
    {
        return this.context.getService( reference );
    }

    @Override
    public void modifiedService( final ServiceReference<Application> reference, final Application application )
    {
    }

    @Override
    public void removedService( final ServiceReference<Application> reference, final Application application )
    {
        // listeners belong to the incarnation whose bootstrap registered them, and this fires
        // synchronously inside unregister() — before a reconfigure's replacement registers. The
        // ApplicationInvalidator round fires after the re-registration and raced the successor's
        // freshly bootstrapped listeners, which is why this manager does not implement it.
        final boolean removed = listeners.removeIf( w -> w.applicationKey.equals( application.getKey() ) );
        if ( removed )
        {
            LOG.info( "Removed all Script Event Listeners for {}", application.getKey() );
        }
        this.context.ungetService( reference );
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
                // backstop: the app's background executor shuts down with its bundle, and an event
                // can still race the tracker-driven removal — a rejected listener is gone anyway
                final boolean removed = listeners.remove( this );
                if ( removed )
                {
                    LOG.info( "Removed Script Event Listener for {}", applicationKey );
                }
            }
        }
    }
}
