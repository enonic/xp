package com.enonic.xp.impl.scheduler.distributed;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.osgi.OsgiSupport;
import com.enonic.xp.impl.scheduler.SchedulerExecutorService;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class RescheduleTask
    implements SchedulableTask
{
    private static final long serialVersionUID = 0;

    public static final String NAME = "rescheduleTask";

    private static final Logger LOG = LoggerFactory.getLogger( RescheduleTask.class );

    private static final AtomicInteger FAILED_COUNT = new AtomicInteger( 0 );

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public void run()
    {
        try
        {
            this.doRun();
            FAILED_COUNT.set( 0 );
        }
        catch ( IllegalStateException e )
        {
            if ( FAILED_COUNT.addAndGet( 1 ) >= 10 )
            {
                FAILED_COUNT.set( 0 );
                LOG.warn( "Problem during tasks scheduling", e );
            } else {
                LOG.debug( "Problem during tasks scheduling", e );
            }
        }
        catch ( Throwable e )
        {
            LOG.error( "Problem during tasks scheduling", e );
        }
    }

    private void doRun()
    {
        final List<ScheduledJob> jobs =
            OsgiSupport.withService( SchedulerService.class, schedulerService -> adminContext().callWith( schedulerService::list ) );

        OsgiSupport.withService( SchedulerExecutorService.class, schedulerExecutorService -> {
            schedulerExecutorService.disposeAllDone();
            return null;
        } );

        final Set<String> scheduledJobs =
            OsgiSupport.withService( SchedulerExecutorService.class, SchedulerExecutorService::getAllFutures );

        LOG.debug( "Currently scheduled jobs {}", scheduledJobs );

        jobs.stream()
            .filter( ScheduledJob::isEnabled )
            .filter( job -> !scheduledJobs.contains( job.getName().getValue() ) )
            .filter( job -> !ScheduleCalendarType.ONE_TIME.equals( job.getCalendar().getType() ) || job.getLastRun() == null )
            .forEach( job -> job.getCalendar().nextExecution().ifPresent( duration -> {
            try
            {
                LOG.debug( "Rescheduling a job {}", job.getName() );
                OsgiSupport.withService( SchedulerExecutorService.class,
                                         scheduler -> scheduler.schedule( SchedulableTaskImpl.create().job( job ).build(),
                                                                          duration.isNegative() ? 0 : duration.toMillis(),
                                                                          TimeUnit.MILLISECONDS ) );
            }
            catch ( Exception e )
            {
                LOG.warn( "{} job rescheduling failed", job.getName(), e );
            }
        } ) );
    }

    private static Context adminContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.ADMIN )
                           .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
                           .build() )
            .build();
    }
}
