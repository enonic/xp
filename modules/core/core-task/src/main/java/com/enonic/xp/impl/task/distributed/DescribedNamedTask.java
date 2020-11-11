package com.enonic.xp.impl.task.distributed;

import java.io.Serializable;
import java.util.UUID;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.task.osgi.OsgiSupport;
import com.enonic.xp.impl.task.script.NamedTask;
import com.enonic.xp.impl.task.script.NamedTaskFactory;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskNotFoundException;

public final class DescribedNamedTask
    implements DescribedTask, Serializable
{
    private static final long serialVersionUID = 0;

    private final TaskId taskId;

    private final String name;

    private final PropertyTree config;

    private final TaskContext context;

    private transient NamedTask namedTask;

    public DescribedNamedTask( final DescriptorKey key, final PropertyTree config, final TaskContext context )
    {
        this.taskId = TaskId.from( UUID.randomUUID().toString() );
        this.name = key.toString();
        this.config = config;
        this.context = context;
    }

    @Override
    public TaskId getTaskId()
    {
        return taskId;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public TaskContext getTaskContext()
    {
        return context;
    }

    @Override
    public String getDescription()
    {
        initNamedTask();
        return namedTask.getTaskDescriptor().getDescription();
    }

    @Override
    public ApplicationKey getApplicationKey()
    {
        initNamedTask();
        return namedTask.getTaskDescriptor().getApplicationKey();
    }

    @Override
    public void run( final ProgressReporter progressReporter )
    {
        initNamedTask();
        namedTask.run( taskId, progressReporter );
    }

    private void initNamedTask()
    {
        if ( namedTask == null )
        {
            final DescriptorKey descriptorKey = DescriptorKey.from( name );
            namedTask = OsgiSupport.withService( NamedTaskFactory.class, ntsf -> ntsf.create( descriptorKey, config ) );
            if ( namedTask == null )
            {
                throw new TaskNotFoundException( descriptorKey );
            }
        }
    }
}
