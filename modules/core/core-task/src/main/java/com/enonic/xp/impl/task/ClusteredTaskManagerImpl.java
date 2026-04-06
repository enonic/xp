package com.enonic.xp.impl.task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.cluster.Member;
import com.hazelcast.cluster.MemberSelector;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.replicatedmap.ReplicatedMap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.Exceptions;
import com.enonic.xp.impl.task.distributed.AllTasksReporter;
import com.enonic.xp.impl.task.distributed.DescribedTask;
import com.enonic.xp.impl.task.distributed.OffloadedTaskCallable;
import com.enonic.xp.impl.task.distributed.RunningTasksReporter;
import com.enonic.xp.impl.task.distributed.SerializableFunction;
import com.enonic.xp.impl.task.distributed.SingleTaskReporter;
import com.enonic.xp.impl.task.distributed.TaskManager;
import com.enonic.xp.impl.task.distributed.TasksReporterCallable;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

@Component(immediate = true, configurationPid = "com.enonic.xp.task")
public final class ClusteredTaskManagerImpl
    implements TaskManager
{
    public static final String ACTION = "xp/task";

    private static final String CLUSTER_MAP_NAME = "com.enonic.xp.cluster";

    private static final String APPLICATION_ATTRIBUTE_PREFIX = "application-";

    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    private final HazelcastInstance hazelcastInstance;

    private final TaskAttributesApplier taskAttributesApplier;

    private IExecutorService executorService;

    private volatile long outboundTimeoutNs;

    @Activate
    public ClusteredTaskManagerImpl( @Reference final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
        this.taskAttributesApplier = new TaskAttributesApplier( hazelcastInstance );
    }

    @Activate
    public void activate( final TaskConfig config )
    {
        outboundTimeoutNs = Duration.parse( config.clustered_timeout() ).toNanos();
        executorService = hazelcastInstance.getExecutorService( ClusteredTaskManagerImpl.ACTION );
        this.taskAttributesApplier.activate( config );
    }

    @Modified
    public void modify( final TaskConfig config )
    {
        outboundTimeoutNs = Duration.parse( config.clustered_timeout() ).toNanos();
        this.taskAttributesApplier.modify( config );
    }

    @Deactivate
    public void deactivate()
    {
        this.taskAttributesApplier.deactivate();
    }

    @Override
    public TaskInfo getTaskInfo( final TaskId taskId )
    {
        final List<TaskInfo> list = send( new SingleTaskReporter( taskId ) );
        return list.isEmpty() ? null : list.getFirst();
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
                final List<TaskInfo> response = responseFuture.get( outboundTimeoutNs, TimeUnit.NANOSECONDS );
                taskInfoBuilder.addAll( response );
            }
            catch ( TimeoutException e )
            {
                resultsFromMembers.values().forEach( f -> f.cancel( true ) );
                throw new IllegalStateException( "Timeout while waiting for task manager response", e );
            }
            catch ( InterruptedException e )
            {
                Thread.currentThread().interrupt();
                throw new IllegalStateException( "Interrupted while waiting for task manager response", e );
            }
            catch ( ExecutionException e )
            {
                throw Exceptions.throwCause( e );
            }
        }
        return taskInfoBuilder;
    }

    @Override
    public void submitTask( final DescribedTask task )
    {
        try
        {
            executorService.submit( new OffloadedTaskCallable( task ), new TaskMemberSelector( task ) )
                .get( outboundTimeoutNs, TimeUnit.NANOSECONDS );
        }
        catch ( TimeoutException e )
        {
            throw new IllegalStateException( "Task submit status unknown due to timeout while waiting for task manager response.", e );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException( "Task submit status unknown due to interruption while waiting for task manager response.", e );
        }
        catch ( ExecutionException e )
        {
            throw Exceptions.throwCause( e );
        }
    }

    private class TaskMemberSelector
        implements MemberSelector
    {
        final DescribedTask task;

        TaskMemberSelector( final DescribedTask task )
        {
            this.task = task;
        }

        @Override
        public boolean select( final Member member )
        {
            final UUID memberUuid = member.getUuid();

            final ReplicatedMap<UUID, Map<String, String>> clusterMap = hazelcastInstance.getReplicatedMap( CLUSTER_MAP_NAME );
            final Map<String, String> clusterAttributes = clusterMap.get( memberUuid );
            if ( clusterAttributes == null ||
                !Boolean.TRUE.toString().equals( clusterAttributes.get( APPLICATION_ATTRIBUTE_PREFIX + task.getApplicationKey() ) ) )
            {
                return false;
            }

            final ReplicatedMap<UUID, Map<String, String>> taskMap = hazelcastInstance.getReplicatedMap( TaskAttributesApplier.MAP_NAME );
            final Map<String, String> taskAttributes = taskMap.get( memberUuid );
            return taskAttributes != null &&
                Boolean.TRUE.toString().equals( taskAttributes.get( TaskAttributesApplier.TASKS_ENABLED_ATTRIBUTE_KEY ) ) &&
                ( !SYSTEM_APPLICATION_KEY.equals( task.getApplicationKey() ) ||
                    Boolean.TRUE.toString().equals( taskAttributes.get( TaskAttributesApplier.SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY ) ) );
        }
    }
}
