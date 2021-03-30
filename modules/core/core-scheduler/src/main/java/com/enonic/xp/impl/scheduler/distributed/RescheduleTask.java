package com.enonic.xp.impl.scheduler.distributed;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    private static final Logger LOG = LoggerFactory.getLogger( RescheduleTask.class );

    @Override
    public String getName()
    {
        return "rescheduleTask";
    }

    @Override
    public void run()
    {
        try
        {
            this.doRun();
        }
        catch ( Exception e )
        {
            LOG.warn( "Problem during task scheduling.", e );
        }
    }

    private void doRun()
    {
        final List<ScheduledJob> jobs = OsgiSupport.withService( SchedulerService.class, schedulerService -> adminContext().
            callWith( schedulerService::list ) );

        OsgiSupport.withService( SchedulerExecutorService.class, schedulerExecutorService -> {
            schedulerExecutorService.disposeAllDone();
            return true;
        } );

        final Set<String> liveTasks =
            OsgiSupport.withService( SchedulerExecutorService.class, ( SchedulerExecutorService::getAllFutures ) );

        final List<ScheduledJob> jobsToSchedule = jobs.
            stream().
            filter( job -> !liveTasks.contains( job.getName().getValue() ) ).
            filter( job -> !ScheduleCalendarType.ONE_TIME.equals( job.getCalendar().getType() ) || job.getLastRun() == null ).
            filter( ScheduledJob::isEnabled ).
            collect( Collectors.toList() );

        jobsToSchedule.forEach( job -> job.getCalendar().
            nextExecution().
            ifPresent( duration -> {
                try
                {
                    OsgiSupport.withService( SchedulerExecutorService.class, scheduler -> scheduler.schedule( SchedulableTaskImpl.create().
                        job( job ).
                        build(), duration.isNegative() ? 0 : duration.toMillis(), TimeUnit.MILLISECONDS ) );
                }
                catch ( Exception e )
                {
                    LOG.warn( "[{}] job rescheduling failed.", job.getName().getValue(), e );
                }
            } ) );
    }

    private static Context adminContext()
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( AuthenticationInfo.create().
                principals( RoleKeys.ADMIN ).
                user( User.create().
                    key( PrincipalKey.ofSuperUser() ).
                    login( PrincipalKey.ofSuperUser().getId() ).
                    build() ).
                build() ).
            build();
    }
}
