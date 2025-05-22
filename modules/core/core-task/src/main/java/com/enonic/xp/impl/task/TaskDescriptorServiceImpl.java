package com.enonic.xp.impl.task;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;

@Component(immediate = true)
public class TaskDescriptorServiceImpl
    implements TaskDescriptorService
{
    private DescriptorService descriptorService;

    @Override
    public Descriptors<TaskDescriptor> getTasks()
    {
        return this.descriptorService.getAll( TaskDescriptor.class );
    }

    @Override
    public Descriptors<TaskDescriptor> getTasks( final ApplicationKey app )
    {
        return this.descriptorService.get( TaskDescriptor.class, ApplicationKeys.from( app ) );
    }

    @Override
    public TaskDescriptor getTask( final DescriptorKey key )
    {
        return this.descriptorService.get( TaskDescriptor.class, key );
    }

    @Reference
    public void setDescriptorService( final DescriptorService descriptorService )
    {
        this.descriptorService = descriptorService;
    }
}
