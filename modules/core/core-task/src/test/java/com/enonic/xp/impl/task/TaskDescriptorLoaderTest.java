package com.enonic.xp.impl.task;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.form.Input;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.task.TaskDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskDescriptorLoaderTest
    extends ApplicationTestSupport
{
    private TaskDescriptorLoader loader;


    @Override
    protected void initialize()
    {
        MixinService mixinService = mock( MixinService.class );
        when( mixinService.inlineFormItems( any() ) ).then( returnsFirstArg() );

        this.loader = new TaskDescriptorLoader( this.resourceService, mixinService );
        addApplication( "myapp1", "/apps/myapp1" );
    }

    @Test
    void testGetType()
    {
        assertEquals( TaskDescriptor.class, this.loader.getType() );
    }

    @Test
    void testPostProcess()
    {
        final TaskDescriptor descriptor = TaskDescriptor.create().key( DescriptorKey.from( "myapp:a" ) ).build();
        assertEquals( descriptor.getKey(), this.loader.postProcess( descriptor ).getKey() );
    }

    @Test
    void testCreateDefault()
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:task1" );
        final TaskDescriptor descriptor = this.loader.createDefault( key );

        assertEquals( key, descriptor.getKey() );
        assertEquals( "task1", descriptor.getName() );
    }

    @Test
    void testFind()
    {
        final DescriptorKeys keys = this.loader.find( ApplicationKey.from( "myapp1" ) );
        assertEquals( 2, keys.getSize() );
        assertTrue( keys.contains( DescriptorKey.from( "myapp1:task1" ) ) );
        assertTrue( keys.contains( DescriptorKey.from( "myapp1:task2" ) ) );
    }

    @Test
    void testLoad()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( "myapp1:task1" );

        final ResourceKey resourceKey = this.loader.toResource( descriptorKey );
        assertEquals( "myapp1:/tasks/task1/task1.xml", resourceKey.toString() );

        final Resource resource = this.resourceService.getResource( resourceKey );
        final TaskDescriptor descriptor = this.loader.load( descriptorKey, resource );

        assertEquals( "MyTask", descriptor.getDescription() );

        Input formItem = descriptor.getConfig().getInput( "param1" );
        assertEquals( " something ", formItem.getDefaultValue().getRootValue() );
    }
}
