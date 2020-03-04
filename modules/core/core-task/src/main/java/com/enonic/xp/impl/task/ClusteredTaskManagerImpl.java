package com.enonic.xp.impl.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.util.ExceptionUtil;

import com.enonic.xp.impl.task.distributed.AllTasksReporter;
import com.enonic.xp.impl.task.distributed.RunningTasksReporter;
import com.enonic.xp.impl.task.distributed.SerializableFunction;
import com.enonic.xp.impl.task.distributed.SingleTaskReporter;
import com.enonic.xp.impl.task.distributed.TasksReporterCallable;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

@Component
public final class ClusteredTaskManagerImpl
    implements ClusteredTaskManager
{
    private static final long TRANSPORT_REQUEST_TIMEOUT_SECONDS = 5L;

    public static final String ACTION = "xp/task";

    private final HazelcastInstance hazelcastInstance;

    private IExecutorService executorService;

    @Activate
    public ClusteredTaskManagerImpl( @Reference final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Activate
    public void activate()
    {
        executorService = hazelcastInstance.getExecutorService( ACTION );
    }

    @Override
    public TaskInfo getTaskInfo( final TaskId taskId )
    {
        final List<TaskInfo> list = send( new SingleTaskReporter( taskId ) );
        return list.isEmpty() ? null : list.get( 0 );
    }

    @Override
    public List<TaskInfo> getRunningTasks()
    {
        return send( new RunningTasksReporter() );
    }

    @Override
    public List<TaskInfo> getAllTasks()
    {
        return send( new AllTasksReporter() );
    }

    private List<TaskInfo> send( final SerializableFunction<TaskManager, List<TaskInfo>> taskFunction )
    {
        final List<TaskInfo> taskInfoBuilder = new ArrayList<>();

        final Map<Member, Future<List<TaskInfo>>> resultsFromMembers =
            executorService.submitToAllMembers( new TasksReporterCallable( taskFunction ) );

        for ( Future<List<TaskInfo>> responseFuture : resultsFromMembers.values() )
        {
            try
            {
                final List<TaskInfo> response = responseFuture.get( TRANSPORT_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS );
                taskInfoBuilder.addAll( response );
            }
            catch ( TimeoutException e )
            {
                resultsFromMembers.values().forEach( f -> f.cancel( true ) );
                throw new RuntimeException( e );
            }
            catch ( InterruptedException | ExecutionException e )
            {
                throw ExceptionUtil.rethrow( e );
            }
        }
        return taskInfoBuilder;
    }
}
