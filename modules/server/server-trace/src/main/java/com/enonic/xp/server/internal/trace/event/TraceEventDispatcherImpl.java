package com.enonic.xp.server.internal.trace.event;

import java.util.concurrent.Executor;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.TraceListener;

@Component
public final class TraceEventDispatcherImpl
    implements TraceEventDispatcher
{
    private final TraceListeners listeners = new TraceListeners();

    private final Executor executor;

    @Activate
    public TraceEventDispatcherImpl( @Reference(service = TraceEventDispatcherExecutor.class) final Executor executor )
    {
        this.executor = executor;
    }

    @Override
    public void queue( final TraceEvent event )
    {
        executor.execute( () -> listeners.onTrace( event ) );
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
