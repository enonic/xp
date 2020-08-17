package com.enonic.xp.server.udc.impl;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.RecurringJob;
import com.enonic.xp.core.internal.concurrent.SimpleRecurringJobScheduler;

@Component
public class UdcSchedulerImpl
    implements UdcScheduler
{
    private static final Logger LOG = LoggerFactory.getLogger( UdcSchedulerImpl.class );

    private final Duration initialDelay;

    private final Duration delay;

    private final SimpleRecurringJobScheduler jobScheduler;

    public UdcSchedulerImpl()
    {
        this( Duration.ofMinutes( 10 ), Duration.ofDays( 1 ) );
    }

    UdcSchedulerImpl( final Duration initialDelay, final Duration delay )
    {
        this.initialDelay = initialDelay;
        this.delay = delay;
        this.jobScheduler = new SimpleRecurringJobScheduler( Executors::newSingleThreadScheduledExecutor, "udc-thread" );
    }

    @Deactivate
    void deactivate()
    {
        jobScheduler.shutdownNow();
    }

    @Override
    public RecurringJob scheduleWithFixedDelay( Runnable command )
    {
        return jobScheduler.
            scheduleWithFixedDelay( command, initialDelay, delay, e -> LOG.debug( "Error error while sending UDC", e ), e -> LOG.error( "Error error while sending UDC, no further attempts will be made", e ) );
    }
}
