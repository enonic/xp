package com.enonic.xp.impl.task;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.RecurringJob;
import com.enonic.xp.core.internal.concurrent.SimpleRecurringJobScheduler;

@Component
public class TaskManagerCleanupSchedulerImpl
    implements TaskManagerCleanupScheduler
{
    private static final Logger LOG = LoggerFactory.getLogger( TaskManagerCleanupSchedulerImpl.class );

    private final SimpleRecurringJobScheduler recurringJobScheduler;

    private final Duration initialDelay;

    private final Duration delay;

    public TaskManagerCleanupSchedulerImpl()
    {
        this( Duration.ofMinutes( 1 ), Duration.ofMinutes( 1 ) );
    }

    TaskManagerCleanupSchedulerImpl( final Duration initialDelay, final Duration delay )
    {
        this.recurringJobScheduler =
            new SimpleRecurringJobScheduler( Executors::newSingleThreadScheduledExecutor, "task-manager-cleanup-thread" );
        this.initialDelay = initialDelay;
        this.delay = delay;
    }

    @Deactivate
    void deactivate()
    {
        recurringJobScheduler.shutdownNow();
    }

    @Override
    public RecurringJob scheduleWithFixedDelay( final Runnable command )
    {
        return recurringJobScheduler.
            scheduleWithFixedDelay( command, initialDelay, delay, e -> LOG.warn( "Error while cleaning up tasks", e ),
                                    e -> LOG.error( "Error while cleaning up tasks, no further attempts will be made", e ) );
    }
}
