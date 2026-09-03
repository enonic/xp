package com.enonic.xp.impl.server.rest.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.task.TaskState;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.web.sse.SseMessage;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.withVirtualHostContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskApiHandlerTest
{
    private TaskService taskService;

    private SseManager sseManager;

    private TaskApiHandler handler;

    @BeforeEach
    void setUp()
    {
        taskService = mock( TaskService.class );
        sseManager = mock( SseManager.class );
        handler = new TaskApiHandler( taskService, sseManager );
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

    @Test
    void watchAll()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:task/watch" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertNotNull( response.getSse() );
        assertTrue( response.getSse().attributes().optional( TaskApiHandler.TASK_ID_ATTRIBUTE ).isEmpty() );

        assertNotNull( handler.handle( request( HttpMethod.GET, "/server:task/events" ) ).getSse() );
    }

    @Test
    void watchOne()
    {
        when( taskService.getTaskInfo( TaskId.from( "t1" ) ) ).thenReturn( task( "t1", TaskState.RUNNING ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:task/t1/watch" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "t1", response.getSse().attributes().property( TaskApiHandler.TASK_ID_ATTRIBUTE ).asString() );

        assertEquals( HttpStatus.NOT_FOUND, handler.handle( request( HttpMethod.GET, "/server:task/t2/watch" ) ).getStatus() );
        assertNull( handler.handle( request( HttpMethod.GET, "/server:task/t2/watch" ) ).getSse() );
    }

    @Test
    void watchIsItsOwnVerb()
    {
        final Map<String, String> policy = Map.of( "api.server:task.verbs", "get, list" );

        assertEquals( HttpStatus.FORBIDDEN, withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.GET, "/server:task/watch" ) ) ).getStatus() );
        assertEquals( HttpStatus.FORBIDDEN, withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.GET, "/server:task/events" ) ) ).getStatus() );
    }

    @Test
    void onSseEvent_openAll_sendsList()
    {
        when( taskService.getAllTasks() ).thenReturn( List.of( task( "t1", TaskState.RUNNING ) ) );
        final UUID clientId = UUID.randomUUID();

        handler.onSseEvent( SseEvent.create().type( SseEventType.OPEN ).clientId( clientId ).attributes( GenericValue.newObject().build() ).build() );

        verify( sseManager ).addToGroup( TaskApiHandler.SSE_GROUP, clientId );
        final ArgumentCaptor<SseMessage> captor = ArgumentCaptor.forClass( SseMessage.class );
        verify( sseManager ).send( eq( clientId ), captor.capture() );
        assertTrue( captor.getValue().toString().contains( "event: list\n" ) );
        assertTrue( captor.getValue().toString().contains( "\"id\":\"t1\"" ) );
    }

    @Test
    void onSseEvent_openOne_sendsTask()
    {
        when( taskService.getTaskInfo( TaskId.from( "t1" ) ) ).thenReturn( task( "t1", TaskState.RUNNING ) );
        final UUID clientId = UUID.randomUUID();

        handler.onSseEvent( SseEvent.create()
                                .type( SseEventType.OPEN )
                                .clientId( clientId )
                                .attributes( GenericValue.newObject().put( TaskApiHandler.TASK_ID_ATTRIBUTE, "t1" ).build() )
                                .build() );

        verify( sseManager ).addToGroup( TaskApiHandler.SSE_GROUP + ":t1", clientId );
        final ArgumentCaptor<SseMessage> captor = ArgumentCaptor.forClass( SseMessage.class );
        verify( sseManager ).send( eq( clientId ), captor.capture() );
        assertTrue( captor.getValue().toString().contains( "event: task\n" ) );
    }

    @Test
    void onSseEvent_close_ignored()
    {
        handler.onSseEvent( SseEvent.create().type( SseEventType.CLOSE ).clientId( UUID.randomUUID() ).attributes( GenericValue.newObject().build() ).build() );

        verify( sseManager, never() ).addToGroup( any(), any() );
    }

    @Test
    void onEvent_forwardsToGroups()
    {
        when( sseManager.getGroupSize( TaskApiHandler.SSE_GROUP ) ).thenReturn( 1 );
        when( sseManager.getGroupSize( TaskApiHandler.SSE_GROUP + ":t1" ) ).thenReturn( 1 );

        handler.onEvent( taskEvent( "task.finished", "t1", false ) );

        final ArgumentCaptor<SseMessage> captor = ArgumentCaptor.forClass( SseMessage.class );
        verify( sseManager ).sendToGroup( eq( TaskApiHandler.SSE_GROUP ), captor.capture() );
        verify( sseManager ).sendToGroup( eq( TaskApiHandler.SSE_GROUP + ":t1" ), any() );
        assertTrue( captor.getValue().toString().contains( "event: finished\n" ) );
        assertTrue( captor.getValue().toString().contains( "\"id\":\"t1\"" ) );
        assertTrue( captor.getValue().toString().contains( "\"state\":\"FINISHED\"" ) );
        assertTrue( captor.getValue().toString().contains( "\"progress\":{" ) );
    }

    @Test
    void onEvent_remoteOriginIsForwarded()
    {
        when( sseManager.getGroupSize( TaskApiHandler.SSE_GROUP ) ).thenReturn( 1 );

        handler.onEvent( taskEvent( "task.updated", "t1", false ) );

        verify( sseManager ).sendToGroup( eq( TaskApiHandler.SSE_GROUP ), any() );
    }

    @Test
    void onEvent_noListeners_noSend()
    {
        handler.onEvent( taskEvent( "task.updated", "t1", true ) );

        verify( sseManager, never() ).sendToGroup( any(), any() );
    }

    @Test
    void onEvent_otherEvents_ignored()
    {
        when( sseManager.getGroupSize( any() ) ).thenReturn( 1 );

        handler.onEvent( Event.create( "application.cluster" ).value( "id", "t1" ).build() );
        handler.onEvent( Event.create( "task.unknown" ).value( "id", "t1" ).build() );

        verify( sseManager, never() ).sendToGroup( any(), any() );
    }

    private static Event taskEvent( final String type, final String id, final boolean local )
    {
        return Event.create( type )
            .localOrigin( local )
            .value( "id", id )
            .value( "name", "com.enonic.xp.app.system:dump" )
            .value( "description", "Dump" )
            .value( "state", "FINISHED" )
            .value( "progress", Map.of( "info", "", "current", 1, "total", 1 ) )
            .value( "application", "com.enonic.xp.app.system" )
            .value( "user", "user:system:su" )
            .value( "startTime", Instant.EPOCH.toString() )
            .build();
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
