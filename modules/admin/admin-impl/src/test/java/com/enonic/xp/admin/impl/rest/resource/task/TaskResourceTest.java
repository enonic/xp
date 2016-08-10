package com.enonic.xp.admin.impl.rest.resource.task;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskManager;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskState;

public class TaskResourceTest
    extends AdminResourceTestSupport
{
    private TaskManager taskManager;

    private TaskResource taskResource;

    @Override
    protected Object getResourceInstance()
    {
        this.taskManager = Mockito.mock( TaskManager.class );

        this.taskResource = new TaskResource();
        taskResource.setTaskManager( this.taskManager );

        return taskResource;
    }

    @Test
    public void list()
        throws Exception
    {
        final TaskId taskId1 = TaskId.from( "123" );
        final TaskInfo taskInfo1 = TaskInfo.create().
            id( taskId1 ).
            description( "My task" ).
            state( TaskState.RUNNING ).
            progress( TaskProgress.create().current( 2 ).total( 10 ).info( "Processing items" ).build() ).
            build();

        final TaskId taskId2 = TaskId.from( "666" );
        final TaskInfo taskInfo2 = TaskInfo.create().
            id( taskId2 ).
            description( "Old task" ).
            state( TaskState.FINISHED ).
            progress( TaskProgress.create().current( 42 ).total( 42 ).info( "Process completed" ).build() ).
            build();

        Mockito.when( this.taskManager.getAllTasks() ).thenReturn( Arrays.asList( taskInfo1, taskInfo2 ) );

        String response = request().path( "tasks" ).get().getAsString();

        assertJson( "get_task_list_result.json", response );
    }

    @Test
    public void getTask()
        throws Exception
    {
        final TaskId taskId = TaskId.from( "123" );
        final TaskInfo taskInfo = TaskInfo.create().
            id( taskId ).
            description( "My task" ).
            state( TaskState.RUNNING ).
            progress( TaskProgress.create().current( 2 ).total( 10 ).info( "Processing items" ).build() ).
            build();

        Mockito.when( this.taskManager.getTaskInfo( taskId ) ).thenReturn( taskInfo );

        String response = request().path( "tasks/" + taskId.toString() ).get().getAsString();

        assertJson( "get_task_result.json", response );
    }

    @Test
    public void getTaskNotFound()
        throws Exception
    {
        final TaskId taskId = TaskId.from( "123" );

        String response = request().path( "tasks/" + taskId.toString() ).get().getAsString();

        assertJson( "get_task_not_found_result.json", response );
    }

}