package com.enonic.xp.impl.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.task.TaskDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskDescriptorServiceImplTest
{
    @Mock
    private DescriptorService descriptorService;

    private TaskDescriptorServiceImpl service;

    @BeforeEach
    void setup()
    {
        this.service = new TaskDescriptorServiceImpl();
        this.service.setDescriptorService( this.descriptorService );
    }

    @Test
    void getTasks()
    {
        final TaskDescriptor desc1 = TaskDescriptor.create().key( DescriptorKey.from( "app:a" ) ).build();

        final TaskDescriptor desc2 = TaskDescriptor.create().key( DescriptorKey.from( "app:b" ) ).build();

        final Descriptors<TaskDescriptor> real = Descriptors.from( desc1, desc2 );
        when( this.descriptorService.getAll( TaskDescriptor.class ) ).thenReturn( real );

        final Descriptors<TaskDescriptor> result1 = this.service.getTasks();
        assertNotNull( result1 );
        assertEquals( 2, result1.getSize() );
    }

    @Test
    void getTasksByApp()
    {
        final TaskDescriptor desc1 = TaskDescriptor.create().key( DescriptorKey.from( "app:a" ) ).build();

        final TaskDescriptor desc2 = TaskDescriptor.create().key( DescriptorKey.from( "app:b" ) ).build();

        final Descriptors<TaskDescriptor> real = Descriptors.from( desc1, desc2 );
        when( this.descriptorService.get( TaskDescriptor.class, ApplicationKeys.from( "app" ) ) ).thenReturn( real );

        final Descriptors<TaskDescriptor> result = this.service.getTasks( ApplicationKey.from( "app" ) );
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }

    @Test
    void getTask()
    {
        final DescriptorKey key = DescriptorKey.from( "app:a" );
        final TaskDescriptor desc = TaskDescriptor.create().key( key ).build();

        when( this.descriptorService.get( TaskDescriptor.class, key ) ).thenReturn( desc );

        final TaskDescriptor result = this.service.getTask( key );
        assertNotNull( result );
    }
}
