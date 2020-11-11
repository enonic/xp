package com.enonic.xp.impl.task;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.internal.concurrent.DynamicReference;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.task.distributed.DescribedNamedTask;
import com.enonic.xp.impl.task.distributed.TaskContext;
import com.enonic.xp.impl.task.distributed.TaskManager;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

@Component(immediate = true, configurationPid = "com.enonic.xp.task")
public final class TaskServiceImpl
    implements TaskService
{
    private static final Logger LOG = LoggerFactory.getLogger( TaskServiceImpl.class );

    private final TaskManager localTaskManager;

    private final DynamicReference<TaskManager> clusteredTaskManagerRef = new DynamicReference<>();

    private volatile boolean acceptOffloaded;

    @Activate
    public TaskServiceImpl( @Reference(target = "(local=true)") final TaskManager localTaskManager )
    {
        this.localTaskManager = localTaskManager;
    }

    @Activate
    @Modified
    public void activate( final TaskConfig config )
    {
        acceptOffloaded = config.offload_acceptInbound();
    }

    @Override
    public TaskId submitTask( final RunnableTask runnable, final String description )
    {
        final DescribedTaskImpl task = new DescribedTaskImpl( runnable, description, buildContext() );
        localTaskManager.submitTask( task );
        return task.getTaskId();
    }

    @Override
    public TaskId submitTask( final DescriptorKey key, final PropertyTree config )
    {
        return submitTask( SubmitTaskParams.create().descriptorKey( key ).config( config ).build() );
    }

    @Override
    public TaskId submitTask( SubmitTaskParams params )
    {
        TaskManager taskManager;
        if ( params.isOffload() )
        {
            taskManager = clusteredTaskManagerRef.getNow( null );
            if ( taskManager == null )
            {
                if ( acceptOffloaded )
                {
                    LOG.debug( "Clustered task manager is unavailable, falling back to local task manager." );
                    taskManager = localTaskManager;
                }
                else
                {
                    try
                    {
                        LOG.info( "Clustered task manager is unavailable, waiting..." );
                        taskManager = clusteredTaskManagerRef.get( 5, TimeUnit.SECONDS );
                    }
                    catch ( InterruptedException | TimeoutException e )
                    {
                        throw new RuntimeException( e );
                    }
                }
            }
        }
        else
        {
            taskManager = localTaskManager;
        }

        final DescribedNamedTask task = new DescribedNamedTask( params.getDescriptorKey(), params.getConfig(), buildContext() );

        taskManager.submitTask( task );
        return task.getTaskId();
    }

    private TaskContext buildContext()
    {
        final Context userContext = ContextAccessor.current();
        return new TaskContext( userContext.getBranch(), userContext.getRepositoryId(), userContext.getAuthInfo() );
    }


    @Override
    public TaskInfo getTaskInfo( final TaskId taskId )
    {
        return getTaskManager().getTaskInfo( taskId );
    }

    @Override
    public List<TaskInfo> getAllTasks()
    {
        return getTaskManager().getAllTasks();
    }

    @Override
    public List<TaskInfo> getRunningTasks()
    {
        return getTaskManager().getRunningTasks();
    }

    private TaskManager getTaskManager()
    {
        return clusteredTaskManagerRef.getNow( localTaskManager );
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, target = "(!(local=true))")
    public void setClusteredTaskManager( final TaskManager taskExecutor )
    {
        this.clusteredTaskManagerRef.set( taskExecutor );
    }

    public void unsetClusteredTaskManager( final TaskManager taskManager )
    {
        this.clusteredTaskManagerRef.reset();
    }
}
