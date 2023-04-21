package com.enonic.xp.impl.scheduler.distributed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.osgi.OsgiSupport;
import com.enonic.xp.impl.scheduler.UpdateLastRunCommand;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.scheduler.CronCalendar;
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

    private static void fetchJobsToSchedule( final Map<ScheduledJobName, ScheduledJob> jobs )
    {
        final Instant now = Instant.now();
        final Set<ScheduledJobName> scheduledJobNames = QUEUE.stream().map( entity -> entity.name ).collect( Collectors.toSet() );

        scheduleCronJobs( jobs, now, scheduledJobNames );
        scheduleOneTimeJobs( jobs, scheduledJobNames );
    }

    private static void scheduleOneTimeJobs( final Map<ScheduledJobName, ScheduledJob> jobs, final Set<ScheduledJobName> scheduledJobNames )
    {
        jobs.values()
            .stream()
            .filter( ScheduledJob::isEnabled )
            .filter( job -> !scheduledJobNames.contains( job.getName() ) )
            .filter( job -> ScheduleCalendarType.ONE_TIME.equals( job.getCalendar().getType() ) && job.getLastRun() == null )
            .forEach( ( job ) -> {
                job.getCalendar()
                    .timeToNextExecution()
                    .ifPresent( duration -> QUEUE.offer( new JobToRun( job.getName(), Instant.now().plus( duration ) ) ) );
            } );
    }

    private static void scheduleCronJobs( final Map<ScheduledJobName, ScheduledJob> jobs, final Instant now,
                                          final Set<ScheduledJobName> scheduledJobNames )
    {
        jobs.values()
            .stream()
            .filter( ScheduledJob::isEnabled )
            .filter( job -> !scheduledJobNames.contains( job.getName() ) )
            .filter( job -> ScheduleCalendarType.CRON.equals( job.getCalendar().getType() ) )
            .forEach( job -> {
                final Instant actualLastRun = job.getLastRun();
                final CronCalendar calendar = (CronCalendar) job.getCalendar();

                if ( actualLastRun != null )
                {
                    calendar.nextExecution( actualLastRun ).ifPresent( nextExecution -> {
                        if ( nextExecution.isBefore( now.atZone( calendar.getTimeZone().toZoneId() ) ) )
                        {
                            QUEUE.offer( new JobToRun( job.getName(), now ) );
                        }
                    } );
                }
                else
                {
                    job.getCalendar()
                        .timeToNextExecution()
                        .ifPresent( nextExecutionDuration -> QUEUE.offer(
                            new JobToRun( job.getName(), Instant.now().plus( nextExecutionDuration ) ) ) );
                }
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
        final Map<ScheduledJobName, ScheduledJob> jobs =
            OsgiSupport.withService( SchedulerService.class, schedulerService -> adminContext().callWith( schedulerService::list ) )
                .stream()
                .collect( Collectors.toMap( ScheduledJob::getName, job -> job ) );

        fetchJobsToSchedule( jobs );

        final List<JobToRun> failedJobs = new ArrayList<>();

        scheduleJobs( jobs, failedJobs );

        retryFailedJobs( failedJobs );
    }

    private void scheduleJobs( final Map<ScheduledJobName, ScheduledJob> jobs, final List<JobToRun> failedJobs )
    {
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

                try
                {
                    final TaskId taskId = taskContext( job.getUser() ).callWith( () -> OsgiSupport.withService( TaskService.class,
                                                                                                                taskService -> taskService.submitTask(
                                                                                                                    SubmitTaskParams.create()
                                                                                                                        .name( job.getName()
                                                                                                                                   .getValue() ) //TODO: check
                                                                                                                        .descriptorKey(
                                                                                                                            job.getDescriptor() )
                                                                                                                        .data(
                                                                                                                            job.getConfig() )
                                                                                                                        .build() ) ) );

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
                    failedJobs.add( peek );
                }
                catch ( Throwable t )
                {
                    LOG.error( "Error while running job [{}], no further attempts will be made", job.getName(), t );
                }
            }
        }
    }

    private void retryFailedJobs( final List<JobToRun> failedJobs )
    {
        failedJobs.forEach( entity -> {
            if ( entity.attempts < 10 )
            {
                QUEUE.offer( new JobToRun( entity.name, entity.timeToRun, entity.attempts + 1 ) );
            }
            else
            {
                LOG.error( "Error while running job [{}], no further attempts will be made", entity.name );
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

        public JobToRun( final ScheduledJobName name, final Instant timeToRun )
        {
            this.name = name;
            this.timeToRun = timeToRun;
            this.attempts = 0;
        }

        public JobToRun( final ScheduledJobName name, final Instant timeToRun, final int attempts )
        {
            this.name = name;
            this.timeToRun = timeToRun;
            this.attempts = attempts;
        }
    }
}
