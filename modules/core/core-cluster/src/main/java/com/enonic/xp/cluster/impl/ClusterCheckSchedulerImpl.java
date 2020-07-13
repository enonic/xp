package com.enonic.xp.cluster.impl;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.RecurringJob;
import com.enonic.xp.core.internal.concurrent.SimpleRecurringJobScheduler;

@Component
public class ClusterCheckSchedulerImpl
    implements ClusterCheckScheduler
{
    private static final Logger LOG = LoggerFactory.getLogger( ClusterCheckSchedulerImpl.class );

    private final SimpleRecurringJobScheduler recurringJobScheduler;

    private final Duration initialDelay;

    private final Duration delay;

    public ClusterCheckSchedulerImpl()
    {
        this( Duration.ZERO, Duration.ofSeconds( 1 ) );
    }

    ClusterCheckSchedulerImpl( final Duration initialDelay, final Duration delay )
    {
        this.recurringJobScheduler = new SimpleRecurringJobScheduler( Executors::newSingleThreadScheduledExecutor, "cluster-check-thread" );
        this.initialDelay = initialDelay;
        this.delay = delay;
    }

    @Deactivate
    void deactivate()
    {
        recurringJobScheduler.shutdownNow();
    }

    @Override
    public RecurringJob scheduleWithFixedDelay( Runnable command )
    {
        return recurringJobScheduler.
            scheduleWithFixedDelay( command, initialDelay, delay, e -> LOG.warn( "Error while checking cluster providers", e ), e -> LOG.error( "Error while checking cluster providers, no further attempts will be made", e ) );
    }
}
