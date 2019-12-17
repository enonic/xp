package com.enonic.xp.impl.task;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.task.TaskDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskDescriptorLoaderTest
    extends ApplicationTestSupport
{
    private TaskDescriptorLoader loader;

    @Override
    protected void initialize()
        throws Exception
    {
        this.loader = new TaskDescriptorLoader();
        this.loader.setResourceService( this.resourceService );

        addApplication( "myapp1", "/apps/myapp1" );
    }

    @Test
    public void testGetType()
    {
        assertEquals( TaskDescriptor.class, this.loader.getType() );
    }

    @Test
    public void testPostProcess()
    {
        final TaskDescriptor descriptor = TaskDescriptor.create().key( DescriptorKey.from( "myapp:a" ) ).build();
        assertSame( descriptor, this.loader.postProcess( descriptor ) );
    }

    @Test
    public void testCreateDefault()
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:task1" );
        final TaskDescriptor descriptor = this.loader.createDefault( key );

        assertEquals( key, descriptor.getKey() );
        assertEquals( "task1", descriptor.getName() );
    }

    @Test
    public void testFind()
    {
        final DescriptorKeys keys = this.loader.find( ApplicationKey.from( "myapp1" ) );
        assertEquals( 2, keys.getSize() );
        assertTrue( keys.contains( DescriptorKey.from( "myapp1:task1" ) ) );
        assertTrue( keys.contains( DescriptorKey.from( "myapp1:task2" ) ) );
    }

    @Test
    public void testLoad()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( "myapp1:task1" );

        final ResourceKey resourceKey = this.loader.toResource( descriptorKey );
        assertEquals( "myapp1:/tasks/task1/task1.xml", resourceKey.toString() );

        final Resource resource = this.resourceService.getResource( resourceKey );
        final TaskDescriptor descriptor = this.loader.load( descriptorKey, resource );

        assertEquals( "MyTask", descriptor.getDescription() );
    }
}
