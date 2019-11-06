package com.enonic.xp.server.internal.trace.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.TraceListener;

@Component
public final class TraceEventDispatcherImpl
    implements TraceEventDispatcher, Runnable
{
    private final TraceListeners listeners = new TraceListeners();

    private final BlockingQueue<TraceEvent> queue = new LinkedBlockingQueue<>();

    private ExecutorService executor;

    @Activate
    public void activate()
    {
        this.executor = Executors.newSingleThreadExecutor();
        this.executor.execute( this );
    }

    @Deactivate
    public void deactivate()
    {
        this.executor.shutdown();
    }

    @Override
    public void queue( final TraceEvent event )
    {
        this.queue.add( event );
    }

    @Override
    public void run()
    {
        while ( true )
        {
            try
            {
                this.listeners.onTrace( this.queue.take() );
            }
            catch ( final InterruptedException e )
            {
                return;
            }
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addListener( final TraceListener listener )
    {
        this.listeners.add( listener );
    }

    public void removeListener( final TraceListener listener )
    {
        this.listeners.remove( listener );
    }
}
