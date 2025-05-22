package com.enonic.xp.impl.task.distributed;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.osgi.OsgiSupport;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.impl.task.script.NamedTaskFactory;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskId;

public final class DistributableTask
    implements DescribedTask, Serializable
{
    private static final long serialVersionUID = 0;

    private final TaskId taskId;

    private final String name;

    private final DescriptorKey key;

    private final TaskContext context;

    private volatile PropertyTree data;

    private transient volatile TaskDescriptor taskDescriptor;

    public DistributableTask( final DescriptorKey key, final String name, final PropertyTree data, final TaskContext context )
    {
        this.taskId = TaskId.from( UUID.randomUUID().toString() );
        this.name = Objects.requireNonNullElseGet( name, key::toString );
        this.key = key;
        this.data = data;
        this.context = context;
    }

    @Override
    public TaskId getTaskId()
    {
        return taskId;
    }

    @Override
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
    public ApplicationKey getApplicationKey()
    {
        return key.getApplicationKey();
    }

    @Override
    public String getDescription()
    {
        initDescriptor();

        return taskDescriptor.getDescription();
    }

    @Override
    public void run( final ProgressReporter progressReporter )
    {
        initDescriptor();

        OsgiSupport.withService( NamedTaskFactory.class, ntsf -> ntsf.create( taskDescriptor, data ) ).run( taskId, progressReporter );
    }

    private synchronized void initDescriptor()
    {
        if ( taskDescriptor == null )
        {
            taskDescriptor = OsgiSupport.withService( TaskDescriptorService.class, tds -> tds.getTask( key ) );

            data = OsgiSupport.withService( PropertyTreeMarshallerService.class,
                                            ptms -> ptms.marshal( data.toMap(), taskDescriptor.getConfig(), true ) );
        }
    }
}
