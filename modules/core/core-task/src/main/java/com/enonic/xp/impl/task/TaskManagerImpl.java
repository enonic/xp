package com.enonic.xp.impl.task;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskState;

import static com.enonic.xp.task.TaskState.FAILED;
import static com.enonic.xp.task.TaskState.FINISHED;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.toList;

@Component
public final class TaskManagerImpl
    implements TaskManager
{
    final static long KEEP_COMPLETED_MAX_TIME_SEC = 60;

    private final ExecutorService executorService;

    private final ConcurrentMap<TaskId, TaskContext> tasks;

    private Supplier<TaskId> idGen;

    private Clock clock;

    public TaskManagerImpl()
    {
        executorService = Executors.newCachedThreadPool();
        tasks = new ConcurrentHashMap<>();
        idGen = this::newId;
        clock = Clock.systemUTC();

        scheduleCleanup();
    }

    private void scheduleCleanup()
    {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool( 1 );
        scheduler.scheduleAtFixedRate( this::removeExpiredTasks, 1, 1, TimeUnit.MINUTES );
    }

    @Override
    public TaskId submitTask( final RunnableTask runnable, final String description )
    {
        final TaskId id = idGen.get();

        final TaskInfo info = TaskInfo.create().
            id( id ).
            description( description ).
            state( TaskState.WAITING ).
            build();

        final TaskContext taskContext = TaskContext.create().
            submitTime( Instant.now( clock ) ).
            taskInfo( info ).
            build();

        tasks.put( id, taskContext );

        final Context userContext = ContextAccessor.current();
        final TaskWrapper wrapper = new TaskWrapper( id, runnable, userContext, this );
        executorService.submit( wrapper );
        return id;
    }

    @Override
    public TaskInfo getTaskInfo( final TaskId taskId )
    {
        final TaskContext ctx = tasks.get( taskId );
        return ctx != null ? ctx.getTaskInfo() : null;
    }

    @Override
    public List<TaskInfo> getAllTasks()
    {
        return tasks.values().stream().map( TaskContext::getTaskInfo ).collect( toList() );
    }

    @Override
    public List<TaskInfo> getRunningTasks()
    {
        return tasks.values().stream().map( TaskContext::getTaskInfo ).filter( TaskInfo::isRunning ).collect( toList() );
    }

    private TaskId newId()
    {
        return TaskId.from( UUID.randomUUID().toString() );
    }

    void setIdGen( final Supplier<TaskId> idGen )
    {
        this.idGen = idGen;
    }

    void updateProgress( final TaskId taskId, final int current, final int total )
    {
        final TaskContext ctx = tasks.get( taskId );
        if ( ctx == null )
        {
            return;
        }
        final TaskInfo taskInfo = ctx.getTaskInfo();
        final TaskProgress updatedProgress = taskInfo.getProgress().copy().current( current ).total( total ).build();

        final TaskInfo updatedInfo = taskInfo.copy().progress( updatedProgress ).build();
        final TaskContext updatedCtx = ctx.copy().taskInfo( updatedInfo ).build();
        tasks.put( taskId, updatedCtx );
    }

    void updateProgress( final TaskId taskId, final String message )
    {
        final TaskContext ctx = tasks.get( taskId );
        if ( ctx == null )
        {
            return;
        }
        final TaskInfo taskInfo = ctx.getTaskInfo();
        final TaskProgress updatedProgress = taskInfo.getProgress().copy().info( message ).build();

        final TaskInfo updatedInfo = taskInfo.copy().progress( updatedProgress ).build();
        final TaskContext updatedCtx = ctx.copy().taskInfo( updatedInfo ).build();
        tasks.put( taskId, updatedCtx );
    }

    void updateState( final TaskId taskId, final TaskState newState )
    {
        final TaskContext ctx = tasks.get( taskId );
        if ( ctx == null )
        {
            return;
        }
        final TaskInfo taskInfo = ctx.getTaskInfo();
        final TaskInfo updatedInfo = taskInfo.copy().state( newState ).build();
        final Instant doneTime = newState == FAILED || newState == FINISHED ? Instant.now( clock ) : null;
        final TaskContext updatedCtx = ctx.copy().taskInfo( updatedInfo ).doneTime( doneTime ).build();
        tasks.put( taskId, updatedCtx );
    }

    void removeExpiredTasks()
    {
        final Instant now = Instant.now( clock );
        for ( TaskContext taskCtx : tasks.values() )
        {
            if ( taskCtx.getTaskInfo().isDone() && taskCtx.getDoneTime() != null &&
                taskCtx.getDoneTime().until( now, SECONDS ) > KEEP_COMPLETED_MAX_TIME_SEC )
            {
                tasks.remove( taskCtx.getTaskInfo().getId() );
            }
        }
    }

    void setClock( final Clock clock )
    {
        this.clock = clock;
    }
}
