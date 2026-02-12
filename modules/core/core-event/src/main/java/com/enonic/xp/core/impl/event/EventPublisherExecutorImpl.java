package com.enonic.xp.core.impl.event;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.SimpleExecutor;

@Component
public class EventPublisherExecutorImpl
    implements EventPublisherExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( EventPublisherExecutorImpl.class );

    private final SimpleExecutor simpleExecutor;

    public EventPublisherExecutorImpl()
    {
        simpleExecutor = SimpleExecutor.ofSingle( "event-publisher-thread", e -> LOG.error( "Event publishing failed", e ) );
    }

    @Deactivate
    public void deactivate()
    {
        simpleExecutor.shutdownAndAwaitTermination( Duration.ofSeconds( 5 ),
                                                    neverCommenced -> LOG.warn( "Not all events were time" ) );
    }

    @Override
    public void execute( final Runnable command )
    {
        simpleExecutor.execute( command );
    }
}
