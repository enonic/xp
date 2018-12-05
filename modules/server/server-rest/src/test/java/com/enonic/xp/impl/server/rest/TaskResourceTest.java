package com.enonic.xp.impl.server.rest;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.task.TaskState;

public class TaskResourceTest
    extends ServerRestTestSupport
{
    private TaskService taskService;

    @Override
    protected Object getResourceInstance()
    {
        this.taskService = Mockito.mock( TaskService.class );

        final TaskResource taskResource = new TaskResource();
        taskResource.setTaskService( this.taskService );

        return taskResource;
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
            application( ApplicationKey.from( "com.enonic.myapp" ) ).
            user( PrincipalKey.from( "user:store:me" ) ).
            startTime( Instant.parse( "2017-10-01T09:00:00Z" ) ).
            progress( TaskProgress.create().current( 2 ).total( 10 ).info( "Processing items" ).build() ).
            build();

        Mockito.when( this.taskService.getTaskInfo( taskId ) ).thenReturn( taskInfo );

        String response = request().path( "task/info" ).queryParam( "id", "123" ).get().getAsString();

        assertJson( "get_task_result.json", response );
    }

    @Test
    public void getTaskNotFound()
        throws Exception
    {
        final TaskId taskId = TaskId.from( "123" );

        String response = request().path( "task/info" ).queryParam( "id", "123" ).get().getAsString();

        assertJson( "get_task_not_found_result.json", response );
    }

}
