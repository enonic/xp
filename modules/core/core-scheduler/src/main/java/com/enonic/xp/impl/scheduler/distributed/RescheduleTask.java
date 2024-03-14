package com.enonic.xp.impl.scheduler.distributed;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.osgi.OsgiSupport;
import com.enonic.xp.impl.scheduler.UpdateLastRunCommand;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.trace.Tracer;

public class RescheduleTask
    implements SchedulableTask
{
    private static final long serialVersionUID = 0;

    public static final String NAME = "rescheduleTask";

    private static final Logger LOG = LoggerFactory.getLogger( RescheduleTask.class );

    private static final AtomicInteger FAILED_COUNT = new AtomicInteger( 0 );

    private static final PriorityQueue<JobToRun> QUEUE = new PriorityQueue<>( Comparator.comparing( a -> a.timeToRun ) );


    @Override
    public String getName()
    {
        return NAME;
    }

    private static void fillJobsToSchedule( final Map<ScheduledJobName, ScheduledJob> jobs )
    {
        final Instant now = Instant.now();

        final Predicate<ScheduledJob> filterAlreadyScheduled =
            job -> !QUEUE.stream().map( entity -> entity.name ).collect( Collectors.toSet() ).contains( job.getName() );

        unscheduleModifiedJobs( jobs );
        scheduleCronJobs( jobs, now, filterAlreadyScheduled );
        scheduleOneTimeJobs( jobs, now, filterAlreadyScheduled );
    }

    private static void unscheduleModifiedJobs( final Map<ScheduledJobName, ScheduledJob> jobs )
    {
        QUEUE.removeIf( jobToRun -> {
            final ScheduledJob scheduledJob = jobs.get( jobToRun.name );
            if ( scheduledJob != null )
            {
                return !jobToRun.modifiedTime.equals( scheduledJob.getModifiedTime() );
            }
            return false;
        } );
    }

    private static void scheduleOneTimeJobs( final Map<ScheduledJobName, ScheduledJob> jobs, final Instant now,
                                             final Predicate<ScheduledJob> filterAlreadyScheduled )
    {
        jobs.values()
            .stream()
            .filter( ScheduledJob::isEnabled )
            .filter( filterAlreadyScheduled )
            .filter( job -> ScheduleCalendarType.ONE_TIME.equals( job.getCalendar().getType() ) && job.getLastRun() == null )
            .forEach( ( job ) -> {
                job.getCalendar().nextExecution( now ).ifPresent( timeToExecute -> QUEUE.offer( new JobToRun( job, timeToExecute ) ) );
            } );
    }

    private static void scheduleCronJobs( final Map<ScheduledJobName, ScheduledJob> jobs, final Instant now,
                                          final Predicate<ScheduledJob> filterAlreadyScheduled )
    {
        jobs.values()
            .stream()
            .filter( ScheduledJob::isEnabled )
            .filter( filterAlreadyScheduled )
            .filter( job -> ScheduleCalendarType.CRON.equals( job.getCalendar().getType() ) )
            .forEach( job -> {
                final Instant actualLastRun = job.getLastRun();

                job.getCalendar().nextExecution( actualLastRun != null ? actualLastRun : now ).ifPresent( timeToExecute -> {
                    QUEUE.offer( new JobToRun( job, timeToExecute ) );
                } );
            } );
    }

    @Override
    public void run()
    {
        try
        {
            this.doRun();
            FAILED_COUNT.set( 0 );
        }
        catch ( Exception e )
        {
            if ( FAILED_COUNT.addAndGet( 1 ) >= 10 )
            {
                FAILED_COUNT.set( 0 );
                LOG.warn( "Problem during tasks scheduling", e );
            }
            else
            {
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
        Tracer.trace( "system.rescheduleTask", () -> {
            final Map<ScheduledJobName, ScheduledJob> jobs =
                OsgiSupport.withService( SchedulerService.class, schedulerService -> adminContext().callWith( schedulerService::list ) )
                    .stream()
                    .collect( Collectors.toMap( ScheduledJob::getName, job -> job ) );

            fillJobsToSchedule( jobs );

            final List<FailedJob> failedJobs = scheduleJobs( jobs );

            retryFailedJobs( failedJobs );
        } );
    }

    private List<FailedJob> scheduleJobs( final Map<ScheduledJobName, ScheduledJob> jobs )
    {
        final ImmutableList.Builder<FailedJob> failedJobs = ImmutableList.builder();

        while ( !QUEUE.isEmpty() )
        {
            final JobToRun peek = QUEUE.peek();

            if ( peek.timeToRun.isAfter( Instant.now() ) )
            {
                break;
            }

            QUEUE.remove();

            if ( jobs.containsKey( peek.name ) ) //there is a job to run
            {
                //submit task

                final ScheduledJob job = jobs.get( peek.name );

                final Function<TaskService, TaskId> submitTask = taskService -> taskService.submitTask(
                    SubmitTaskParams.create().descriptorKey( job.getDescriptor() ).data( job.getConfig() ).build() );
                try
                {
                    final TaskId taskId =
                        taskContext( job.getUser() ).callWith( () -> OsgiSupport.withService( TaskService.class, submitTask ) );

                    adminContext().runWith( () -> OsgiSupport.withService( NodeService.class, nodeService -> UpdateLastRunCommand.create()
                        .nodeService( nodeService )
                        .name( job.getName() )
                        .lastRun( Instant.now() )
                        .lastTaskId( taskId )
                        .build()
                        .execute() ) );
                }
                catch ( Exception e )
                {
                    failedJobs.add( new FailedJob( peek, e ) );
                }
                catch ( Throwable t )
                {
                    LOG.error( "Error while running job [{}], no further attempts will be made", job.getName(), t );
                }
            }
        }

        return failedJobs.build();
    }

    private void retryFailedJobs( final List<FailedJob> failedJobs )
    {
        failedJobs.forEach( failedJob -> {
            if ( failedJob.job.attempts < 10 )
            {
                QUEUE.offer(
                    new JobToRun( failedJob.job.name, failedJob.job.modifiedTime, failedJob.job.timeToRun, failedJob.job.attempts + 1 ) );
                LOG.warn( "Error while running job [{}], will try to run once more", failedJob.job.name );
            }
            else
            {
                adminContext().runWith( () -> OsgiSupport.withService( NodeService.class, nodeService -> UpdateLastRunCommand.create()
                    .nodeService( nodeService )
                    .name( failedJob.job.name )
                    .lastRun( Instant.now() )
                    .lastTaskId( null )
                    .build()
                    .execute() ) );

                LOG.error( "Error while running job [{}], no further attempts will be made", failedJob.job.name, failedJob.reason );
            }
        } );
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

    private Context taskContext( final PrincipalKey user )
    {
        if ( user == null )
        {
            return ContextBuilder.from( ContextAccessor.current() ).authInfo( AuthenticationInfo.unAuthenticated() ).build();
        }

        final AuthenticationInfo authInfo = OsgiSupport.withService( SecurityService.class, securityService -> {
            final VerifiedUsernameAuthToken token = new VerifiedUsernameAuthToken();
            token.setIdProvider( user.getIdProviderKey() );
            token.setUsername( user.getId() );

            return securityService.authenticate( token );
        } );

        return ContextBuilder.from( ContextAccessor.current() ).authInfo( authInfo ).build();
    }

    private static class JobToRun
    {
        private final ScheduledJobName name;

        private final Instant timeToRun;

        private final int attempts;

        private final Instant modifiedTime;

        JobToRun( final ScheduledJob job, final Instant timeToRun )
        {
            this.name = job.getName();
            this.modifiedTime = job.getModifiedTime();
            this.timeToRun = timeToRun;
            this.attempts = 0;
        }

        JobToRun( final ScheduledJobName name, final Instant modifiedTime, final Instant timeToRun, final int attempts )
        {
            this.name = name;
            this.modifiedTime = modifiedTime;
            this.timeToRun = timeToRun;
            this.attempts = attempts;
        }
    }

    private static class FailedJob
    {
        final JobToRun job;

        final Exception reason;

        FailedJob( final JobToRun job, final Exception reason )
        {
            this.job = job;
            this.reason = reason;
        }
    }

}
