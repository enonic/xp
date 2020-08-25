package com.enonic.xp.impl.task;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.task.script.NamedTaskScriptFactory;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskNotFoundException;
import com.enonic.xp.task.TaskService;

import static com.enonic.xp.impl.task.script.NamedTaskScript.SCRIPT_METHOD_NAME;

@Component
public final class TaskServiceImpl
    implements TaskService
{
    private final TaskManager taskManager;

    private final TaskDescriptorService taskDescriptorService;

    private final NamedTaskScriptFactory namedTaskScriptFactory;

    private volatile TaskInfoManager taskInfoManager;

    @Activate
    public TaskServiceImpl( @Reference final TaskManager taskManager, @Reference final TaskDescriptorService taskDescriptorService,
                            @Reference final NamedTaskScriptFactory namedTaskScriptFactory )
    {
        this.taskManager = taskManager;
        this.taskInfoManager = taskManager;
        this.taskDescriptorService = taskDescriptorService;
        this.namedTaskScriptFactory = namedTaskScriptFactory;
    }

    @Override
    public TaskId submitTask( final RunnableTask runnable, final String description )
    {
        return taskManager.submitTask( runnable, description, "" );
    }

    @Override
    public TaskId submitTask( final DescriptorKey key, final PropertyTree config )
    {
        final TaskDescriptor descriptor = taskDescriptorService.getTasks( key.getApplicationKey() ).
            filter( ( taskDesc ) -> taskDesc.getKey().equals( key ) ).first();
        if ( descriptor == null )
        {
            throw new TaskNotFoundException( key );
        }

        final RunnableTask runnableTask = namedTaskScriptFactory.create( descriptor, config );
        if ( runnableTask == null )
        {
            throw new TaskNotFoundException( key, "Missing exported function '" + SCRIPT_METHOD_NAME + "' in task script" );
        }

        return taskManager.submitTask( runnableTask, descriptor.getDescription(), key.toString() );
    }

    @Override
    public TaskInfo getTaskInfo( final TaskId taskId )
    {
        return taskInfoManager.getTaskInfo( taskId );
    }

    @Override
    public List<TaskInfo> getAllTasks()
    {
        return taskInfoManager.getAllTasks();
    }

    @Override
    public List<TaskInfo> getRunningTasks()
    {
        return taskInfoManager.getRunningTasks();
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setClusteredTaskManager( final ClusteredTaskManager clusteredTaskManager )
    {
        this.taskInfoManager = clusteredTaskManager;
    }

    public void unsetClusteredTaskManager( final ClusteredTaskManager clusteredTaskManager )
    {
        this.taskInfoManager = taskManager;
    }
}
