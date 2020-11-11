package com.enonic.xp.impl.task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.util.ExceptionUtil;

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

    private final HazelcastInstance hazelcastInstance;

    private IExecutorService executorService;

    private volatile long outboundTimeoutNs;

    @Activate
    public ClusteredTaskManagerImpl( @Reference final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Activate
    public void activate( final TaskConfig config )
    {
        outboundTimeoutNs = Duration.parse( config.offload_outboundTimeout() ).toNanos();
        executorService = hazelcastInstance.getExecutorService( ClusteredTaskManagerImpl.ACTION );
    }

    @Modified
    public void modify( final TaskConfig config )
    {
        outboundTimeoutNs = Duration.parse( config.offload_outboundTimeout() ).toNanos();
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
                final List<TaskInfo> response = responseFuture.get( outboundTimeoutNs, TimeUnit.NANOSECONDS );
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

    @Override
    public void submitTask( final DescribedTask task )
    {
        try
        {
            executorService.submit( new OffloadedTaskCallable( task ), new TaskMemberSelector( task ) ).
                get( outboundTimeoutNs, TimeUnit.NANOSECONDS );
        }
        catch ( TimeoutException e )
        {
            throw new RuntimeException( e );
        }
        catch ( InterruptedException | ExecutionException e )
        {
            throw ExceptionUtil.rethrow( e );
        }
    }

    private static class TaskMemberSelector
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
            return Boolean.TRUE.equals( member.getBooleanAttribute( MemberAttributesApplier.TASKS_ENABLED_ATTRIBUTE_KEY ) ) &&
                Boolean.TRUE.equals(
                    member.getBooleanAttribute( MemberAttributesApplier.TASKS_ENABLED_ATTRIBUTE_PREFIX + task.getApplicationKey() ) );
        }
    }

    public void setMemberAttributesApplier( @Reference final MemberAttributesApplier memberAttributesApplier )
    {
        // Bogus dependency to make sure memberAttributesApplier got activated first.
        // Hopefully OSGi R8 Conditions will make it simpler.
    }
}
