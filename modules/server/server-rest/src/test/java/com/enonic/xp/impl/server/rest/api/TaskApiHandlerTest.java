package com.enonic.xp.impl.server.rest.api;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.task.TaskState;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskApiHandlerTest
{
    private TaskService taskService;

    private TaskApiHandler handler;

    @BeforeEach
    void setUp()
    {
        taskService = mock( TaskService.class );
        handler = new TaskApiHandler( taskService );
    }

    @Test
    void get()
    {
        when( taskService.getTaskInfo( TaskId.from( "t1" ) ) ).thenReturn( task( "t1", TaskState.RUNNING ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:task/t1" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( String.valueOf( response.getBody() ).contains( "\"id\":\"t1\"" ) );
        assertEquals( HttpStatus.NOT_FOUND, handler.handle( request( HttpMethod.GET, "/server:task/t2" ) ).getStatus() );
    }

    @Test
    void list()
    {
        when( taskService.getAllTasks() ).thenReturn( List.of( task( "t1", TaskState.FINISHED ), task( "t2", TaskState.RUNNING ) ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:task" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( String.valueOf( response.getBody() ).contains( "\"id\":\"t1\"" ) );
        assertTrue( String.valueOf( response.getBody() ).contains( "\"id\":\"t2\"" ) );
    }

    @Test
    void listRunning()
    {
        when( taskService.getRunningTasks() ).thenReturn( List.of( task( "t2", TaskState.RUNNING ) ) );

        final WebRequest request = request( HttpMethod.GET, "/server:task" );
        request.getParams().put( "running", "true" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        verify( taskService ).getRunningTasks();
        assertTrue( String.valueOf( response.getBody() ).contains( "\"id\":\"t2\"" ) );
    }

    private static TaskInfo task( final String id, final TaskState state )
    {
        return TaskInfo.create()
            .id( TaskId.from( id ) )
            .state( state )
            .name( id )
            .description( id )
            .application( ApplicationKey.from( "com.enonic.xp.app.system" ) )
            .startTime( Instant.EPOCH )
            .build();
    }
}
