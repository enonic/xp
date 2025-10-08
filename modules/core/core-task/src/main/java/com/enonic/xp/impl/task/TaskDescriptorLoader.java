package com.enonic.xp.impl.task;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.task.TaskDescriptor;

@Component(immediate = true)
public final class TaskDescriptorLoader
    implements DescriptorLoader<TaskDescriptor>
{
    private static final String PATH = "/tasks";

    private final DescriptorKeyLocator descriptorKeyLocator;

    private final MixinService mixinService;

    @Activate
    public TaskDescriptorLoader( @Reference final ResourceService resourceService, @Reference final MixinService mixinService )
    {
        this.descriptorKeyLocator = new DescriptorKeyLocator( resourceService, PATH, false );
        this.mixinService = mixinService;
    }

    @Override
    public Class<TaskDescriptor> getType()
    {
        return TaskDescriptor.class;
    }

    @Override
    public DescriptorKeys find( final ApplicationKey key )
    {
        return descriptorKeyLocator.findKeys( key );
    }

    @Override
    public ResourceKey toResource( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), PATH + "/" + key.getName() + "/" + key.getName() + ".yml" );
    }

    @Override
    public TaskDescriptor load( final DescriptorKey key, final Resource resource )
    {
        return YmlTaskDescriptorParser.parse( resource.readString(), key.getApplicationKey() ).key( key ).build();
    }

    @Override
    public TaskDescriptor createDefault( final DescriptorKey key )
    {
        return TaskDescriptor.create().key( key ).description( key.getName() ).build();
    }

    @Override
    public TaskDescriptor postProcess( final TaskDescriptor descriptor )
    {
        return TaskDescriptor.create()
            .key( descriptor.getKey() )
            .description( descriptor.getDescription() )
            .config( this.mixinService.inlineFormItems( descriptor.getConfig() ) )
            .build();
    }
}
