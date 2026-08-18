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
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeIdExistsException;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
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

    private final SchedulerConfig schedulerConfig;

    private final Clock clock;

    private final Map<ScheduledJobName, FailedSubmits> failedSubmits = new HashMap<>();

    private final Map<ScheduledJobName, RunState> runStates = new HashMap<>();

    private final Set<ScheduledJobName> dormantJobs = new HashSet<>();

    private final Map<ScheduledJobName, String> submittedTaskIds = new HashMap<>();

    private final Set<ScheduledJobName> configuredJobsCreated = new HashSet<>();

    private final Set<ScheduledJobName> configuredJobsFailed = new HashSet<>();

    private Set<ScheduledJobName> knownJobs = Set.of();

    private int failedTicks;

    public RescheduleTask( final SchedulerServiceImpl schedulerService, final NodeService nodeService, final TaskService taskService,
                           final SecurityService securityService, final ClusterService clusterService,
                           final SchedulingCoordinator schedulingCoordinator, final SchedulerConfig schedulerConfig, final Clock clock )
    {
        this.schedulerService = schedulerService;
        this.nodeService = nodeService;
        this.taskService = taskService;
        this.securityService = securityService;
        this.clusterService = clusterService;
        this.schedulingCoordinator = schedulingCoordinator;
        this.schedulerConfig = schedulerConfig;
        this.clock = clock;
    }

    @Override
    public void run()
    {
        try
        {
            if ( schedulingCoordinator.isLeader() )
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
        createConfiguredJobs( jobNames );

        runStates.keySet().retainAll( jobNames );
        dormantJobs.retainAll( jobNames );
        submittedTaskIds.keySet().retainAll( jobNames );
        failedSubmits.keySet()
            .retainAll( entries.stream().map( ScheduledJobEntry::job ).filter( ScheduledJob::isEnabled ).map( ScheduledJob::getName )
                            .collect( Collectors.toSet() ) );

        entries.stream()
            .filter( entry -> entry.job().isEnabled() )
            .flatMap( entry -> dueTime( entry, now ).map( dueTime -> Map.entry( dueTime, entry ) ).stream() )
            .sorted( Map.Entry.comparingByKey() )
            .forEach( entry -> submit( entry.getValue() ) );
    }

    /**
     * Creates the jobs this node has in its configuration, from the tick rather than when the bundle
     * starts: the member that schedules is not known that early, and only the one that does should
     * be writing jobs. A member that never leads never writes its configured jobs, so which node
     * holds them is a configuration choice like any other.
     * <p>
     * The configuration is read every tick, so a job added to it is created without a restart. Each
     * name is created once - a job deleted afterwards stays deleted, rather than being put back a
     * second later by the node that made it. The record of what was created is this member's own, so
     * a new leader takes one pass to learn that the jobs are already there, and will recreate one
     * that was deleted in the meantime, as a restart would have.
     */
    private void createConfiguredJobs( final Set<ScheduledJobName> existingJobs )
    {
        for ( final CreateScheduledJobParams job : schedulerConfig.jobs() )
        {
            final ScheduledJobName name = job.getName();
            if ( configuredJobsCreated.contains( name ) )
            {
                continue;
            }
            if ( existingJobs.contains( name ) )
            {
                configuredJobsCreated.add( name );
                continue;
            }
            try
            {
                adminContext().runWith( () -> schedulerService.create( job ) );
                configuredJobsCreated.add( name );
                configuredJobsFailed.remove( name );
            }
            catch ( NodeAlreadyExistAtPathException | NodeIdExistsException e )
            {
                // the listing was taken before the job appeared - another member's last tick, say
                LOG.debug( "Configured job [{}] already exists", name, e );
                configuredJobsCreated.add( name );
            }
            catch ( Exception e )
            {
                // one job that cannot be created is not allowed to keep the others from running. It
                // is reported once and then retried quietly, so correcting the configuration is
                // enough to get it created - no restart, and no message every second until then
                if ( configuredJobsFailed.add( name ) )
                {
                    LOG.error( "Failed to create configured job [{}], will keep trying", name, e );
                }
                else
                {
                    LOG.debug( "Failed to create configured job [{}]", name, e );
                }
            }
        }
    }

    private Optional<Instant> dueTime( final ScheduledJobEntry entry, final Instant now )
    {
        final ScheduledJob job = entry.job();
        final Instant dueTime;
        if ( job.getCalendar().getType() == ScheduleCalendarType.ONE_TIME )
        {
            // a one-time job's lastRun tombstone lives in node data (#12271), so the listed job
            // carries it; the coordinator covers the window until the tombstone is persisted
            if ( plannedRun( entry ) != null || job.getLastRun() != null )
            {
                return Optional.empty();
            }
            return Optional.of( ( (OneTimeCalendar) job.getCalendar() ).getValue() )
                .filter( due -> !due.isAfter( now ) )
                // a tombstone left as a version attribute by an earlier version is invisible to the
                // listing, so confirm with a full read rather than run the job a second time - the
                // filter above keeps that to jobs otherwise about to run, and it is memoized on the
                // job's version
                .filter( due -> runState( entry ).lastRun() == null );
        }
        else
        {
            final PlannedRun plannedRun = plannedRun( entry );
            if ( plannedRun != null )
            {
                dueTime = plannedRun.nextRun();
            }
            else
            {
                final Instant lastRun = runState( entry ).lastRun();
                // rebuilding a lost plan needs the same guard the plan itself is built with: asked
                // for what follows a run it already made, a calendar may answer with that very
                // instant (#10854) and the occurrence would be submitted a second time
                dueTime = lastRun != null
                    ? nextExecutionAfter( job.getCalendar(), lastRun )
                    // a job that has never run catches up from when it was last modified (#10125),
                    // where an occurrence falling on that instant is the first one, not a repeat
                    : job.getCalendar().nextExecution( baseline( job, now ) ).orElse( null );
            }
        }
        return Optional.ofNullable( dueTime ).filter( due -> !due.isAfter( now ) );
    }

    /**
     * The job's planned run, or null when there is none or when the one there describes a version
     * of the job that has since been replaced - a modified job re-arms rather than inheriting.
     */
    private PlannedRun plannedRun( final ScheduledJobEntry entry )
    {
        final PlannedRun plannedRun = schedulingCoordinator.plannedRun( entry.job().getName() );
        return plannedRun != null && Objects.equals( plannedRun.versionId(), versionOf( entry ) ) ? plannedRun : null;
    }

    private static String versionOf( final ScheduledJobEntry entry )
    {
        return entry.versionId() != null ? entry.versionId().toString() : null;
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
                planNextRun( entry, nextExecutionAfter( job.getCalendar(), now ), previousTaskId );
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
            final int attempts = countFailedSubmit( entry );
            if ( attempts > MAX_SUBMIT_ATTEMPTS )
            {
                failedSubmits.remove( job.getName() );
                recordRun( entry, null );
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
            recordRun( entry, null );
            LOG.error( "Error while running job [{}], no further attempts will be made", job.getName(), e );
            return;
        }

        failedSubmits.remove( job.getName() );
        submittedTaskIds.put( job.getName(), taskId.toString() );
        recordRun( entry, taskId );
    }

    private void recordRun( final ScheduledJobEntry entry, final TaskId taskId )
    {
        final ScheduledJob job = entry.job();

        // timed here rather than when the job's turn came: resolving the application, the previous
        // task and the user all take time, and a clustered submission can block until it times out
        final Instant now = Instant.now( clock );

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
        planNextRun( entry, type == ScheduleCalendarType.ONE_TIME ? now : nextExecutionAfter( job.getCalendar(), now ),
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

    private void planNextRun( final ScheduledJobEntry entry, final Instant nextRun, final String lastTaskId )
    {
        final ScheduledJobName name = entry.job().getName();
        try
        {
            if ( nextRun != null )
            {
                schedulingCoordinator.plannedRun( name, new PlannedRun( nextRun, lastTaskId, versionOf( entry ) ) );
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

    private int countFailedSubmit( final ScheduledJobEntry entry )
    {
        // attempts are counted against the job as it was when they were made: a job that has been
        // modified starts over, so fixing one that had used up its attempts does not have its very
        // first failure treated as its last
        final FailedSubmits previous = failedSubmits.get( entry.job().getName() );
        final int attempts =
            previous != null && Objects.equals( previous.versionId(), entry.versionId() ) ? previous.attempts() + 1 : 1;
        failedSubmits.put( entry.job().getName(), new FailedSubmits( entry.versionId(), attempts ) );
        return attempts;
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
        final ScheduledJobName name = entry.job().getName();
        final PlannedRun plannedRun = plannedRun( entry );
        if ( plannedRun != null )
        {
            return plannedRun.lastTaskId();
        }
        // a fixed-rate job persists no run state, so once a shared plan is lost - a Hazelcast
        // restart, say - this leader's own memory is all that stands between it and an overlap
        final String persisted = runState( entry ).lastTaskId();
        return persisted != null ? persisted : submittedTaskIds.get( name );
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

    private record FailedSubmits(NodeVersionId versionId, int attempts)
    {
    }
}
