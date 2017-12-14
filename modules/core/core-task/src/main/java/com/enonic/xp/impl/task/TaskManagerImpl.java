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
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.impl.task.event.TaskEvents;
import com.enonic.xp.impl.task.script.NamedTaskScript;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskState;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

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

    private EventPublisher eventPublisher;

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
    public TaskId submitTask( final RunnableTask runnable, final String description, String name )
    {
        final Trace trace = Tracer.newTrace( "task.submit" );
        if ( trace == null )
        {
            return doSubmitTask( runnable, description, name );
        }

        final TaskId id = Tracer.trace( trace, () -> doSubmitTask( runnable, description, name ) );
        trace.put( "taskId", id );
        trace.put( "name", name );
        return id;
    }

    private TaskId doSubmitTask( final RunnableTask runnable, final String description, String name )
    {
        final TaskId id = idGen.get();

        final Context userContext = ContextAccessor.current();

        final User user = userContext.getAuthInfo().getUser();
        final TaskInfo info = TaskInfo.create().
            id( id ).
            description( description ).
            name( name ).
            state( TaskState.WAITING ).
            startTime( Instant.now() ).
            application( getApplication( runnable ) ).
            user( user != null ? user.getKey() : PrincipalKey.ofAnonymous() ).
            build();

        final TaskContext taskContext = TaskContext.create().
            submitTime( Instant.now( clock ) ).
            taskInfo( info ).
            build();

        tasks.put( id, taskContext );

        eventPublisher.publish( TaskEvents.submitted( info ) );

        final TaskWrapper wrapper = new TaskWrapper( info, runnable, userContext, this );
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

    private ApplicationKey getApplication( final RunnableTask runnable )
    {
        try
        {
            if ( runnable instanceof NamedTaskScript )
            {
                return ( (NamedTaskScript) runnable ).getApplication();
            }
            return ApplicationKey.from( runnable.getClass() );
        }
        catch ( Exception e )
        {
            return null;
        }
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

        eventPublisher.publish( TaskEvents.updated( updatedInfo ) );
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

        eventPublisher.publish( TaskEvents.updated( updatedInfo ) );
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
        }
    }

    void removeExpiredTasks()
    {
        final Instant now = Instant.now( clock );
        for ( TaskContext taskCtx : tasks.values() )
        {
            final TaskInfo taskInfo = taskCtx.getTaskInfo();
            if ( taskInfo.isDone() && taskCtx.getDoneTime() != null &&
                taskCtx.getDoneTime().until( now, SECONDS ) > KEEP_COMPLETED_MAX_TIME_SEC )
            {
                tasks.remove( taskInfo.getId() );
                eventPublisher.publish( TaskEvents.removed( taskInfo ) );
            }
        }
    }

    void setClock( final Clock clock )
    {
        this.clock = clock;
    }

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }
}
