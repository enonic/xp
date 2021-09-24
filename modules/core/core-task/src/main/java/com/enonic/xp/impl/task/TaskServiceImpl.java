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
import com.enonic.xp.impl.task.distributed.DescribedTask;
import com.enonic.xp.impl.task.distributed.DistributableTask;
import com.enonic.xp.impl.task.distributed.TaskContext;
import com.enonic.xp.impl.task.distributed.TaskManager;
import com.enonic.xp.impl.task.script.NamedTask;
import com.enonic.xp.impl.task.script.NamedTaskFactory;
import com.enonic.xp.node.NodePath;
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

    private final NamedTaskFactory namedTaskFactory;

    private volatile boolean acceptOffloaded;

    @Activate
    public TaskServiceImpl( @Reference(target = "(local=true)") final TaskManager localTaskManager,
                            @Reference final NamedTaskFactory namedTaskFactory )
    {
        this.localTaskManager = localTaskManager;
        this.namedTaskFactory = namedTaskFactory;
    }

    @Activate
    @Modified
    public void activate( final TaskConfig config )
    {
        acceptOffloaded = config.distributable_acceptInbound();
    }

    @Override
    public TaskId submitTask( final RunnableTask runnable, final String description )
    {
        final DescribedTaskImpl task = new DescribedTaskImpl( runnable, description, buildContext() );
        return submitLocal( task );
    }

    @Override
    public TaskId submitTask( final DescriptorKey key, final PropertyTree config )
    {
        final NamedTask namedTask = namedTaskFactory.createLegacy( key, config );
        final DescribedTaskImpl task = new DescribedTaskImpl( namedTask, buildContext() );
        return submitLocal( task );
    }

    @Override
    public TaskId submitTask( SubmitTaskParams params )
    {
        TaskManager taskManager;
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

        final DistributableTask task = new DistributableTask( params.getDescriptorKey(), params.getData(), buildContext() );

        taskManager.submitTask( task );
        return task.getTaskId();
    }

    private TaskContext buildContext()
    {
        final Context userContext = ContextAccessor.current();
        return TaskContext.create()
            .setBranch( userContext.getBranch() )
            .setRepo( userContext.getRepositoryId() )
            .setAuthInfo( userContext.getAuthInfo() )
            .setContentRootPath( (NodePath) ContextAccessor.current().getAttribute( "contentRootPath" ) )
            .build();
    }

    private TaskId submitLocal( final DescribedTask task )
    {
        localTaskManager.submitTask( task );
        return task.getTaskId();
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
