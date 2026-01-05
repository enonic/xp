package com.enonic.xp.impl.task;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.core.internal.Local;
import com.enonic.xp.core.internal.concurrent.RecurringJob;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.impl.task.distributed.DescribedTask;
import com.enonic.xp.impl.task.distributed.TaskManager;
import com.enonic.xp.impl.task.event.TaskEvents;
import com.enonic.xp.security.User;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskState;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.trace.Tracer;

@Component(immediate = true)
@Local
public final class LocalTaskManagerImpl
    implements TaskManager
{
    static final long KEEP_COMPLETED_MAX_TIME_SEC = 60;

    private final ConcurrentMap<TaskId, TaskInfoHolder> tasks = new ConcurrentHashMap<>();

    private final EventPublisher eventPublisher;

    private final TaskManagerCleanupScheduler cleanupScheduler;

    private final Executor executor;

    static Clock clock = Clock.systemUTC();

    private RecurringJob cleaner;

    private volatile ClusterConfig clusterConfig;

    @Activate
    public LocalTaskManagerImpl( @Reference(service = TaskManagerExecutor.class) final Executor executor,
                                 @Reference TaskManagerCleanupScheduler cleanupScheduler, @Reference final EventPublisher eventPublisher )
    {
        this.executor = executor;
        this.cleanupScheduler = cleanupScheduler;
        this.eventPublisher = eventPublisher;
    }

    @Activate
    public void activate()
    {
        cleaner = cleanupScheduler.scheduleWithFixedDelay( this::removeExpiredTasks );
    }

    @Deactivate
    public void deactivate()
    {
        cleaner.cancel();
    }

    @Override
    public TaskInfo getTaskInfo( final TaskId taskId )
    {
        final TaskInfoHolder ctx = tasks.get( taskId );
        return ctx != null ? ctx.getTaskInfo() : null;
    }

    @Override
    public List<TaskInfo> getAllTasks()
    {
        return tasks.values().stream().map( TaskInfoHolder::getTaskInfo ).collect( Collectors.toUnmodifiableList() );
    }

    @Override
    public List<TaskInfo> getRunningTasks()
    {
        return tasks.values()
            .stream()
            .map( TaskInfoHolder::getTaskInfo )
            .filter( TaskInfo::isRunning )
            .collect( Collectors.toUnmodifiableList() );
    }

    private void updateProgress( final TaskId taskId, final Integer current, final Integer total, final String info )
    {
        final TaskInfoHolder ctx = tasks.get( taskId );
        if ( ctx == null )
        {
            return;
        }
        final TaskInfo taskInfo = ctx.getTaskInfo();
        final TaskProgress.Builder updatedProgress = taskInfo.getProgress().copy();

        if ( current != null )
        {
            updatedProgress.current( current );
        }
        if ( total != null )
        {
            updatedProgress.total( total );
        }
        if ( info != null )
        {
            updatedProgress.info( info );
        }

        final TaskInfo updatedInfo = taskInfo.copy().progress( updatedProgress.build() ).build();
        final TaskInfoHolder updatedCtx = ctx.copy().taskInfo( updatedInfo ).build();
        tasks.put( taskId, updatedCtx );

        eventPublisher.publish( TaskEvents.updated( updatedInfo ) );
    }

    private void updateState( final TaskId taskId, final TaskState newState )
    {
        final TaskInfoHolder ctx = tasks.get( taskId );
        if ( ctx == null )
        {
            return;
        }
        final TaskInfo taskInfo = ctx.getTaskInfo();
        final TaskInfo updatedInfo = taskInfo.copy().state( newState ).build();
        final Instant doneTime = newState == TaskState.FAILED || newState == TaskState.FINISHED ? Instant.now( clock ) : null;
        final TaskInfoHolder updatedCtx = ctx.copy().taskInfo( updatedInfo ).doneTime( doneTime ).build();
        tasks.put( taskId, updatedCtx );

        switch ( newState )
        {
            case FINISHED:
                eventPublisher.publish( TaskEvents.finished( updatedInfo ) );
                break;
            case FAILED:
                eventPublisher.publish( TaskEvents.failed( updatedInfo ) );
                break;
            default:
                eventPublisher.publish( TaskEvents.updated( updatedInfo ) );
                break;
        }
    }

    @Override
    public void submitTask( final DescribedTask runnableTask )
    {
        Tracer.trace( "task.submit", trace -> {
            trace.put( "taskId", runnableTask.getTaskId() );
            trace.put( "name", runnableTask.getName() );
        }, () -> doSubmitTask( runnableTask ) );
    }

    private void doSubmitTask( final DescribedTask runnableTask )
    {
        final TaskId id = runnableTask.getTaskId();
        final User user = runnableTask.getTaskContext().getAuthInfo() != null ? Objects.requireNonNullElse(
            runnableTask.getTaskContext().getAuthInfo().getUser(), User.ANONYMOUS ) : User.ANONYMOUS;
        final TaskInfo info = TaskInfo.create()
            .id( id )
            .description( runnableTask.getDescription() )
            .name( runnableTask.getName() )
            .state( TaskState.WAITING )
            .startTime( Instant.now( clock ) )
            .application( runnableTask.getApplicationKey() )
            .node( clusterConfig == null ? null : clusterConfig.name() )
            .user( user.getKey() )
            .build();

        final TaskInfoHolder taskInfoHolder = TaskInfoHolder.create().taskInfo( info ).build();

        tasks.put( id, taskInfoHolder );

        eventPublisher.publish( TaskEvents.submitted( info ) );

        executor.execute( new TaskRunnable( runnableTask, new ProgressReporterAdapter( id ) ) );
    }

    private void removeExpiredTasks()
    {
        final Instant now = Instant.now( clock );
        for ( TaskInfoHolder taskCtx : tasks.values() )
        {
            final TaskInfo taskInfo = taskCtx.getTaskInfo();
            if ( taskInfo.isDone() && taskCtx.getDoneTime() != null &&
                taskCtx.getDoneTime().until( now, ChronoUnit.SECONDS ) > KEEP_COMPLETED_MAX_TIME_SEC )
            {
                tasks.remove( taskInfo.getId() );
                eventPublisher.publish( TaskEvents.removed( taskInfo ) );
            }
        }
    }

    private class ProgressReporterAdapter
        implements InternalProgressReporter
    {
        private final TaskId taskId;

        ProgressReporterAdapter( final TaskId taskId )
        {
            this.taskId = taskId;
        }

        public void running()
        {
            updateState( taskId, TaskState.RUNNING );
        }

        @Override
        public void finished()
        {
            updateState( taskId, TaskState.FINISHED );
        }

        @Override
        public void failed( final String message )
        {
            updateProgress( taskId, null, null, message );
            updateState( taskId, TaskState.FAILED );
        }

        @Override
        public void progress( final int current, final int total )
        {
            updateProgress( taskId, current, total, null );
        }

        @Override
        public void progress( final Integer current, final Integer total, final String message )
        {
            updateProgress( taskId, current, total, message );
        }

        @Override
        public void progress( final ProgressReportParams params )
        {
            updateProgress( taskId, params.getCurrent(), params.getTotal(), params.getInfo() );
        }

        @Override
        public void info( final String message )
        {
            updateProgress( taskId, null, null, message );
        }
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setClusterConfig( final ClusterConfig clusterConfig )
    {
        this.clusterConfig = clusterConfig;
    }

    public void unsetClusterConfig( final ClusterConfig clusterConfig )
    {
        // Keep previous ClusterConfig until new is set
    }
}
