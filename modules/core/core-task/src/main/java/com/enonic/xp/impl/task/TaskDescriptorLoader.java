package com.enonic.xp.impl.task;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.task.TaskDescriptor;

@Component(immediate = true)
public final class TaskDescriptorLoader
    implements DescriptorLoader<TaskDescriptor>
{
    private static final String PATH = "/tasks";

    private final DescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public TaskDescriptorLoader( @Reference final ResourceService resourceService )
    {
        descriptorKeyLocator = new DescriptorKeyLocator( resourceService, PATH, false );
    }

    @Override
    public Class<TaskDescriptor> getType()
    {
        return TaskDescriptor.class;
    }

    @Override
    public DescriptorKeys find( final ApplicationKey key )
    {
        return DescriptorKeys.from( descriptorKeyLocator.findKeys( key ) );
    }

    @Override
    public ResourceKey toResource( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), PATH + "/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    public TaskDescriptor load( final DescriptorKey key, final Resource resource )
    {
        final TaskDescriptor.Builder builder = TaskDescriptor.create();
        builder.key( key );

        final String descriptorXml = resource.readString();
        parseXml( key.getApplicationKey(), builder, descriptorXml );
        return builder.build();
    }

    @Override
    public TaskDescriptor createDefault( final DescriptorKey key )
    {
        return TaskDescriptor.create().key( key ).description( key.getName() ).build();
    }

    @Override
    public TaskDescriptor postProcess( final TaskDescriptor descriptor )
    {
        return descriptor;
    }

    private void parseXml( final ApplicationKey applicationKey, final TaskDescriptor.Builder builder, final String xml )
    {
        final XmlTaskDescriptorParser parser = new XmlTaskDescriptorParser();
        parser.builder( builder );
        parser.currentApplication( applicationKey );
        parser.source( xml );
        parser.parse();
    }
}
