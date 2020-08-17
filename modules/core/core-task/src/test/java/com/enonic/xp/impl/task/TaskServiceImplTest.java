package com.enonic.xp.impl.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.impl.task.script.NamedTaskScriptFactory;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest
{

    @Mock
    private TaskManager taskManager;

    @Mock
    private TaskDescriptorService taskDescriptorService;

    @Mock
    private NamedTaskScriptFactory namedTaskScriptFactory;

    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp()
    {
        taskService = new TaskServiceImpl( taskManager, taskDescriptorService, namedTaskScriptFactory );
    }

    @Test
    void submitTask_runnableTask()
    {
        final TaskId someTaskId = TaskId.from( "someId" );
        when( taskManager.submitTask( any( RunnableTask.class ), eq( "someDescription" ), eq( "" ) ) ).thenReturn( someTaskId );

        final TaskId taskId = taskService.submitTask( mock( RunnableTask.class ), "someDescription" );

        assertEquals( taskId, someTaskId );
    }

    @Test
    void submitTask_DescriptorKey()
    {
        final TaskId someTaskId = TaskId.from( "someId" );
        final DescriptorKey descriptorKey = DescriptorKey.from( "module:my-admin-tool" );
        final PropertyTree config = new PropertyTree();

        final TaskDescriptor taskDescriptor = TaskDescriptor.create().
            description( "someDescription" ).
            key( descriptorKey ).
            build();

        when( taskDescriptorService.getTasks( ApplicationKey.from( "module" ) ) ).thenReturn( Descriptors.from( taskDescriptor ) );

        final RunnableTask runnableTask = mock( RunnableTask.class );

        when( namedTaskScriptFactory.create( same( taskDescriptor ), same( config ) ) ).thenReturn( runnableTask );

        when( taskManager.submitTask( same( runnableTask ), eq( "someDescription" ), eq( descriptorKey.toString() ) ) ).
            thenReturn( someTaskId );
        final TaskId taskId = taskService.submitTask( descriptorKey, config );

        assertEquals( taskId, someTaskId );
    }

    @Test
    void getTasks()
    {
        final TaskId someTaskId1 = TaskId.from( "someTask1" );

        taskService.getTaskInfo( someTaskId1 );

        verify( taskManager ).getTaskInfo( eq( someTaskId1 ) );

        final ClusteredTaskManager clusteredTaskManager = mock( ClusteredTaskManager.class );

        taskService.setClusteredTaskManager( clusteredTaskManager );

        taskService.getAllTasks();

        verify( clusteredTaskManager ).getAllTasks();

        taskService.unsetClusteredTaskManager( clusteredTaskManager );

        taskService.getRunningTasks();

        verifyNoMoreInteractions( clusteredTaskManager );

        verify( taskManager ).getRunningTasks();
    }
}
