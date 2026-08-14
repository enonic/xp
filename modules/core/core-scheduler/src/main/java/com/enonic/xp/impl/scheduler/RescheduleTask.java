package com.enonic.xp.impl.scheduler;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendar;
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
import com.enonic.xp.trace.Traced;

public final class RescheduleTask
    implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger( RescheduleTask.class );

    private static final int MAX_SUBMIT_ATTEMPTS = 10;

    private static final int FAILED_TICKS_TO_WARN = 10;

    private final SchedulerService schedulerService;

    private final NodeService nodeService;

    private final TaskService taskService;

    private final SecurityService securityService;

    private final ClusterService clusterService;

    private final SchedulingCoordinator schedulingCoordinator;

    private final Clock clock;

    private final Map<ScheduledJobName, Integer> failedSubmits = new HashMap<>();

    private int failedTicks;

    public RescheduleTask( final SchedulerService schedulerService, final NodeService nodeService, final TaskService taskService,
                           final SecurityService securityService, final ClusterService clusterService,
                           final SchedulingCoordinator schedulingCoordinator, final Clock clock )
    {
        this.schedulerService = schedulerService;
        this.nodeService = nodeService;
        this.taskService = taskService;
        this.securityService = securityService;
        this.clusterService = clusterService;
        this.schedulingCoordinator = schedulingCoordinator;
        this.clock = clock;
    }

    @Override
    public void run()
    {
        try
        {
            if ( clusterService.isLeader() )
            {
                doRun();
            }
            failedTicks = 0;
        }
        catch ( Exception e )
        {
            if ( ++failedTicks >= FAILED_TICKS_TO_WARN )
            {
                failedTicks = 0;
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

    @Traced("system.rescheduleTask")
    private void doRun()
    {
        final Instant now = Instant.now( clock );

        final List<ScheduledJob> jobs = adminContext().callWith( schedulerService::list );

        schedulingCoordinator.retain( jobs.stream().map( ScheduledJob::getName ).collect( Collectors.toSet() ) );
        failedSubmits.keySet()
            .retainAll( jobs.stream().filter( ScheduledJob::isEnabled ).map( ScheduledJob::getName ).collect( Collectors.toSet() ) );

        jobs.stream()
            .filter( ScheduledJob::isEnabled )
            .flatMap( job -> dueTime( job, now ).map( dueTime -> Map.entry( dueTime, job ) ).stream() )
            .sorted( Map.Entry.comparingByKey() )
            .forEach( entry -> submit( entry.getValue(), now ) );
    }

    private Optional<Instant> dueTime( final ScheduledJob job, final Instant now )
    {
        final Instant dueTime;
        if ( job.getCalendar().getType() == ScheduleCalendarType.ONE_TIME )
        {
            dueTime = job.getLastRun() == null && schedulingCoordinator.nextRun( job.getName() ) == null
                ? ( (OneTimeCalendar) job.getCalendar() ).getValue()
                : null;
        }
        else
        {
            final Instant plannedNextRun = schedulingCoordinator.nextRun( job.getName() );
            dueTime = plannedNextRun != null ? plannedNextRun : job.getCalendar()
                .nextExecution( job.getLastRun() != null ? job.getLastRun() : baseline( job, now ) )
                .orElse( null );
        }
        return dueTime != null && !dueTime.isAfter( now ) ? Optional.of( dueTime ) : Optional.empty();
    }

    private static Instant baseline( final ScheduledJob job, final Instant now )
    {
        return job.getModifiedTime() != null ? job.getModifiedTime() : now;
    }

    private void submit( final ScheduledJob job, final Instant now )
    {
        final TaskId taskId;
        try
        {
            taskId = taskContext( job.getUser() ).callWith( () -> taskService.submitTask(
                SubmitTaskParams.create().descriptorKey( job.getDescriptor() ).data( job.getConfig() ).build() ) );
        }
        catch ( Exception e )
        {
            final int attempts = failedSubmits.merge( job.getName(), 1, Integer::sum );
            if ( attempts > MAX_SUBMIT_ATTEMPTS )
            {
                failedSubmits.remove( job.getName() );
                recordRun( job, now, null );
                LOG.error( "Error while running job [{}], no further attempts will be made", job.getName(), e );
            }
            else
            {
                LOG.warn( "Error while running job [{}], will try to run once more", job.getName(), e );
            }
            return;
        }
        catch ( Throwable e )
        {
            failedSubmits.remove( job.getName() );
            recordRun( job, now, null );
            LOG.error( "Error while running job [{}], no further attempts will be made", job.getName(), e );
            return;
        }

        failedSubmits.remove( job.getName() );
        recordRun( job, now, taskId );
    }

    private void recordRun( final ScheduledJob job, final Instant now, final TaskId taskId )
    {
        // for cron jobs the shared value plans the next execution; for one-time jobs it marks the only execution as submitted
        schedulingCoordinator.nextRun( job.getName(), job.getCalendar().getType() == ScheduleCalendarType.CRON
            ? nextExecutionAfter( job.getCalendar(), now )
            : now );
        try
        {
            adminContext().runWith( () -> UpdateLastRunCommand.create()
                .nodeService( nodeService )
                .name( job.getName() )
                .lastRun( now )
                .lastTaskId( taskId )
                .build()
                .execute() );
        }
        catch ( Exception e )
        {
            LOG.warn( "Failed to store last run of job [{}]", job.getName(), e );
        }
    }

    static Instant nextExecutionAfter( final ScheduleCalendar calendar, final Instant now )
    {
        return calendar.nextExecution( now )
            .map( next -> next.isAfter( now )
                ? next
                : calendar.nextExecution( now.truncatedTo( ChronoUnit.SECONDS ).plusSeconds( 1 ) ).orElse( null ) )
            .orElse( null );
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

        final AuthenticationInfo authInfo =
            securityService.authenticate( new VerifiedUsernameAuthToken( user.getIdProviderKey(), user.getId() ) );

        return ContextBuilder.from( ContextAccessor.current() ).authInfo( authInfo ).build();
    }
}
