package com.enonic.xp.impl.scheduler;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.impl.scheduler.distributed.PlannedRun;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.trace.Traced;

public final class RescheduleTask
    implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger( RescheduleTask.class );

    private static final int MAX_SUBMIT_ATTEMPTS = 10;

    private static final int FAILED_TICKS_TO_WARN = 10;

    static final String SCHEDULE_LAST_TASK_ID_ATTRIBUTE = "schedule.lastTaskId";

    private final SchedulerServiceImpl schedulerService;

    private final NodeService nodeService;

    private final TaskService taskService;

    private final SecurityService securityService;

    private final ClusterService clusterService;

    private final SchedulingCoordinator schedulingCoordinator;

    private final Clock clock;

    private final Map<ScheduledJobName, Integer> failedSubmits = new HashMap<>();

    private final Map<ScheduledJobName, RunState> runStates = new HashMap<>();

    private final Set<ScheduledJobName> dormantJobs = new HashSet<>();

    private Set<ScheduledJobName> knownJobs = Set.of();

    private int failedTicks;

    private volatile boolean suspended;

    private volatile boolean resetPending;

    public RescheduleTask( final SchedulerServiceImpl schedulerService, final NodeService nodeService, final TaskService taskService,
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

    /**
     * Stops scheduling until {@link #resume()}, e.g. while a repository restore replaces the
     * jobs underneath the scheduler.
     */
    public void suspend()
    {
        suspended = true;
    }

    /**
     * Resumes scheduling, discarding everything learned about jobs that may no longer be the
     * jobs that were there before.
     */
    public void resume()
    {
        resetPending = true;
        suspended = false;
    }

    @Override
    public void run()
    {
        if ( suspended )
        {
            return;
        }
        try
        {
            if ( resetPending )
            {
                resetPending = false;
                forgetJobs();
            }
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

    private void forgetJobs()
    {
        // restored jobs may share names with the ones they replaced, so neither the shared plans
        // nor anything memoized about them can be assumed to describe the jobs that are there now
        runStates.clear();
        dormantJobs.clear();
        failedSubmits.clear();
        knownJobs = Set.of();
        schedulingCoordinator.retain( Set.of() );
    }

    @Traced("system.rescheduleTask")
    private void doRun()
    {
        final Instant now = Instant.now( clock );

        // run state (lastRun) is deliberately not fetched here: the coordinator and the version-keyed
        // run-state memo make one version read per job suffice, instead of one per job every tick
        final List<ScheduledJobEntry> entries = adminContext().callWith( schedulerService::listEntries );

        final Set<ScheduledJobName> jobNames =
            entries.stream().map( entry -> entry.job().getName() ).collect( Collectors.toSet() );
        if ( !jobNames.equals( knownJobs ) )
        {
            schedulingCoordinator.retain( jobNames );
            knownJobs = jobNames;
        }
        runStates.keySet().retainAll( jobNames );
        dormantJobs.retainAll( jobNames );
        failedSubmits.keySet()
            .retainAll( entries.stream().map( ScheduledJobEntry::job ).filter( ScheduledJob::isEnabled ).map( ScheduledJob::getName )
                            .collect( Collectors.toSet() ) );

        entries.stream()
            .filter( entry -> entry.job().isEnabled() )
            .flatMap( entry -> dueTime( entry, now ).map( dueTime -> Map.entry( dueTime, entry ) ).stream() )
            .sorted( Map.Entry.comparingByKey() )
            .forEach( entry -> submit( entry.getValue() ) );
    }

    private Optional<Instant> dueTime( final ScheduledJobEntry entry, final Instant now )
    {
        final ScheduledJob job = entry.job();
        final Instant dueTime;
        if ( job.getCalendar().getType() == ScheduleCalendarType.ONE_TIME )
        {
            // a one-time job's lastRun tombstone lives in node data (#12271), so the listed job
            // carries it; the coordinator covers the window until the tombstone is persisted
            if ( schedulingCoordinator.plannedRun( job.getName() ) != null || job.getLastRun() != null )
            {
                return Optional.empty();
            }
            final Instant value = ( (OneTimeCalendar) job.getCalendar() ).getValue();
            // a tombstone left as a version attribute by an earlier version is invisible to the
            // listing, so confirm with a full read rather than run the job a second time - only
            // once the job is otherwise about to run, and memoized on its version
            return !value.isAfter( now ) && runState( entry ).lastRun() == null ? Optional.of( value ) : Optional.empty();
        }
        else
        {
            final PlannedRun plannedRun = schedulingCoordinator.plannedRun( job.getName() );
            if ( plannedRun != null )
            {
                dueTime = plannedRun.nextRun();
            }
            else
            {
                final Instant lastRun = runState( entry ).lastRun();
                dueTime = job.getCalendar().nextExecution( lastRun != null ? lastRun : baseline( job, now ) ).orElse( null );
            }
        }
        return dueTime != null && !dueTime.isAfter( now ) ? Optional.of( dueTime ) : Optional.empty();
    }

    private RunState runState( final ScheduledJobEntry entry )
    {
        final ScheduledJobName name = entry.job().getName();
        RunState runState = runStates.get( name );
        if ( runState == null || !Objects.equals( runState.versionId(), entry.versionId() ) )
        {
            final ScheduledJob fetched = adminContext().callWith( () -> schedulerService.get( name ) );
            runState = new RunState( entry.versionId(), fetched != null ? fetched.getLastRun() : null,
                                     fetched != null && fetched.getLastTaskId() != null ? fetched.getLastTaskId().toString() : null );
            runStates.put( name, runState );
        }
        return runState;
    }

    private static Instant baseline( final ScheduledJob job, final Instant now )
    {
        return job.getModifiedTime() != null ? job.getModifiedTime() : now;
    }

    private void submit( final ScheduledJobEntry entry )
    {
        final ScheduledJob job = entry.job();

        // the tick's own instant decides which jobs are due, but a run is recorded and its
        // successor planned from the moment the run actually starts - by then the tick may have
        // spent time listing jobs and submitting the ones that sorted earlier
        final Instant now = Instant.now( clock );

        // only a missing application makes a job dormant. A descriptor missing from an application
        // that is running is a misconfigured job, and fails loudly rather than lying dormant
        if ( !clusterService.inCluster( job.getDescriptor().getApplicationKey() ) )
        {
            // the application has not started yet, or is no longer installed anywhere - the job
            // waits without failing and fires as soon as the application is back
            if ( dormantJobs.add( job.getName() ) )
            {
                LOG.warn( "Application [{}] of job [{}] is not started - the job is dormant until it is",
                          job.getDescriptor().getApplicationKey(), job.getName() );
            }
            return;
        }
        if ( dormantJobs.remove( job.getName() ) )
        {
            LOG.info( "Application [{}] of job [{}] is started again", job.getDescriptor().getApplicationKey(), job.getName() );
        }

        final String previousTaskId = previousTaskId( entry );

        if ( previousTaskId != null && !previousTaskDone( previousTaskId ) )
        {
            // non-overlapping execution (#12272): the previous run is still in flight
            if ( job.getCalendar().getType() == ScheduleCalendarType.CRON )
            {
                // cron occurrences are calendar positions - a blocked one is skipped
                planNextRun( job.getName(), nextExecutionAfter( job.getCalendar(), now ), previousTaskId );
            }
            // a fixed-rate run holds: it stays due and fires once the previous task finishes
            return;
        }

        final TaskId taskId;
        try
        {
            taskId = taskContext( job.getUser(), previousTaskId ).callWith( () -> taskService.submitTask(
                SubmitTaskParams.create().descriptorKey( job.getDescriptor() ).data( job.getConfig() ).build() ) );
        }
        catch ( Exception e )
        {
            final int attempts = failedSubmits.merge( job.getName(), 1, Integer::sum );
            if ( attempts > MAX_SUBMIT_ATTEMPTS )
            {
                failedSubmits.remove( job.getName() );
                recordRun( entry, now, null );
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
            recordRun( entry, now, null );
            LOG.error( "Error while running job [{}], no further attempts will be made", job.getName(), e );
            return;
        }

        failedSubmits.remove( job.getName() );
        recordRun( entry, now, taskId );
    }

    private void recordRun( final ScheduledJobEntry entry, final Instant now, final TaskId taskId )
    {
        final ScheduledJob job = entry.job();

        // a job can be modified, or deleted and recreated, while one of its runs is in flight.
        // Whatever schedule is there now owns the plan and the record: modify() has already
        // discarded the plan of the job this run belonged to, and must not have it written back
        if ( !Objects.equals( entry.versionId(), currentVersionId( job.getName() ) ) )
        {
            LOG.debug( "Job [{}] changed while its run was in flight - the run is not recorded", job.getName() );
            runStates.remove( job.getName() );
            return;
        }

        final ScheduleCalendarType type = job.getCalendar().getType();
        // the shared value plans the next execution; for a one-time job it marks the only execution as submitted
        planNextRun( job.getName(), type == ScheduleCalendarType.ONE_TIME ? now : nextExecutionAfter( job.getCalendar(), now ),
                     taskId != null ? taskId.toString() : null );
        if ( type == ScheduleCalendarType.CRON )
        {
            runStates.put( job.getName(),
                           new RunState( entry.versionId(), now, taskId != null ? taskId.toString() : null ) );
        }
        if ( type == ScheduleCalendarType.FIXED_RATE )
        {
            // fixed-rate jobs persist no run state (#12271): executions simply follow one another
            return;
        }
        if ( job.getCalendar() instanceof OneTimeCalendar oneTime && oneTime.isDeleteAfterRun() && taskId != null &&
            deleteAfterRun( job.getName() ) )
        {
            // an ephemeral one-time job (#12274) is removed instead of leaving a tombstone behind; a job
            // that never ran is kept, since nothing else in the system would record that it failed
            return;
        }
        try
        {
            adminContext().runWith( () -> UpdateLastRunCommand.create()
                .nodeService( nodeService )
                .name( job.getName() )
                .expectedVersionId( entry.versionId() )
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

    private void planNextRun( final ScheduledJobName name, final Instant nextRun, final String lastTaskId )
    {
        try
        {
            if ( nextRun != null )
            {
                schedulingCoordinator.plannedRun( name, new PlannedRun( nextRun, lastTaskId ) );
            }
            else
            {
                // the calendar has no further occurrence - drop the plan instead of keeping a stale one
                schedulingCoordinator.forget( name );
            }
        }
        catch ( Exception e )
        {
            // the persisted run state still bounds the next execution, so recording it is worth
            // attempting even when the shared plan could not be written
            LOG.warn( "Failed to share the planned run of job [{}]", name, e );
        }
    }

    private NodeVersionId currentVersionId( final ScheduledJobName name )
    {
        return adminContext().callWith( () -> schedulerService.versionId( name ) );
    }

    private boolean deleteAfterRun( final ScheduledJobName name )
    {
        try
        {
            adminContext().runWith( () -> schedulerService.delete( name ) );
            return true;
        }
        catch ( Exception e )
        {
            // fall back to recording the run, so that a failed cleanup can never become a re-run
            LOG.warn( "Failed to delete job [{}] after its run, the last run is recorded instead", name, e );
            return false;
        }
    }

    private String previousTaskId( final ScheduledJobEntry entry )
    {
        if ( entry.job().getCalendar().getType() == ScheduleCalendarType.ONE_TIME )
        {
            return null;
        }
        final PlannedRun plannedRun = schedulingCoordinator.plannedRun( entry.job().getName() );
        return plannedRun != null ? plannedRun.lastTaskId() : runState( entry ).lastTaskId();
    }

    private boolean previousTaskDone( final String previousTaskId )
    {
        final TaskInfo taskInfo = taskService.getTaskInfo( TaskId.from( previousTaskId ) );
        return taskInfo == null || taskInfo.isDone();
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

    private Context taskContext( final PrincipalKey user, final String previousTaskId )
    {
        final AuthenticationInfo authInfo = user == null
            ? AuthenticationInfo.unAuthenticated()
            : securityService.authenticate( new VerifiedUsernameAuthToken( user.getIdProviderKey(), user.getId() ) );

        final ContextBuilder context = ContextBuilder.from( ContextAccessor.current() ).authInfo( authInfo );
        if ( previousTaskId != null )
        {
            context.attribute( SCHEDULE_LAST_TASK_ID_ATTRIBUTE, previousTaskId );
        }
        return context.build();
    }

    private record RunState(NodeVersionId versionId, Instant lastRun, String lastTaskId)
    {
    }
}
