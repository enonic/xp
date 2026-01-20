package com.enonic.xp.server.internal.trace.event;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.SimpleExecutor;

@Component
public class TraceEventDispatcherExecutorImpl
    implements TraceEventDispatcherExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( TraceEventDispatcherExecutorImpl.class );

    private final SimpleExecutor simpleExecutor;

    public TraceEventDispatcherExecutorImpl()
    {
        simpleExecutor = SimpleExecutor.ofSingle( "trace-event-dispatcher-thread", e -> LOG.error( "Trace event dispatch failed", e ) );
    }

    @Deactivate
    public void deactivate()
    {
        // Trace events are not mission critical, so we don't bother waiting for their delivery.
        simpleExecutor.shutdownAndAwaitTermination( Duration.ZERO, neverCommenced -> LOG.warn( "Not all trace events were dispatched" ) );
    }

    @Override
    public void execute( final Runnable command )
    {
        simpleExecutor.execute( command );
    }
}
