package com.enonic.xp.impl.task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.TaskDescriptor;

import static org.junit.Assert.*;

public class TaskDescriptorServiceImplTest
{
    private DescriptorService descriptorService;

    private TaskDescriptorServiceImpl service;

    @Before
    public void setup()
    {
        this.descriptorService = Mockito.mock( DescriptorService.class );

        this.service = new TaskDescriptorServiceImpl();
        this.service.setDescriptorService( this.descriptorService );
    }

    @Test
    public void getTasks()
        throws Exception
    {
        final TaskDescriptor desc1 = TaskDescriptor.create().key( DescriptorKey.from( "app:a" ) ).build();

        final TaskDescriptor desc2 = TaskDescriptor.create().key( DescriptorKey.from( "app:b" ) ).build();

        final Descriptors<TaskDescriptor> real = Descriptors.from( desc1, desc2 );
        Mockito.when( this.descriptorService.getAll( TaskDescriptor.class ) ).thenReturn( real );

        final Descriptors<TaskDescriptor> result1 = this.service.getTasks();
        assertNotNull( result1 );
        assertEquals( 2, result1.getSize() );
    }

    @Test
    public void getTasksByApp()
        throws Exception
    {
        final TaskDescriptor desc1 = TaskDescriptor.create().key( DescriptorKey.from( "app:a" ) ).build();

        final TaskDescriptor desc2 = TaskDescriptor.create().key( DescriptorKey.from( "app:b" ) ).build();

        final Descriptors<TaskDescriptor> real = Descriptors.from( desc1, desc2 );
        Mockito.when( this.descriptorService.get( TaskDescriptor.class, ApplicationKeys.from( "app" ) ) ).thenReturn( real );

        final Descriptors<TaskDescriptor> result = this.service.getTasks( ApplicationKey.from( "app" ) );
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }
}
