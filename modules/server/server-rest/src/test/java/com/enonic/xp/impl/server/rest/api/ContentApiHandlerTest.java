package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.withVirtualHostContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContentApiHandlerTest
{
    private TaskService taskService;

    private ContentApiHandler handler;

    @BeforeEach
    void setUp()
    {
        taskService = mock( TaskService.class );
        handler = new ContentApiHandler( mock( ProjectService.class ), mock( SyncContentService.class ), taskService );
        when( taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "t1" ) );
    }

    @Test
    void syncAll()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/server:content/sync" ) );

        assertEquals( HttpStatus.ACCEPTED, response.getStatus() );
        assertEquals( "{\"taskId\":\"t1\"}", response.getBody() );
        assertEquals( "Sync all projects", submitted().getDescription() );
    }

    @Test
    void syncNamed()
    {
        handler.handle( request( HttpMethod.POST, "/server:content/sync", "{\"projects\":[\"intranet\"]}" ) );

        assertEquals( "Sync projects [intranet]", submitted().getDescription() );
    }

    @Test
    void syncRejectsInvalidProjectName()
    {
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:content/sync", "{\"projects\":[\"Not A Project\"]}" ) ).getStatus() );
        verify( taskService, never() ).submitLocalTask( any() );
    }

    @Test
    void syncIsLocal()
    {
        handler.handle( request( HttpMethod.POST, "/server:content/sync" ) );

        verify( taskService ).submitLocalTask( any() );
        verify( taskService, never() ).submitTask( any() );
    }

    @Test
    void policy()
    {
        final Map<String, String> locked = Map.of( "api.server:content.verbs", "-" );

        assertEquals( HttpStatus.FORBIDDEN, withVirtualHostContext( locked, () -> handler.handle( request( HttpMethod.POST, "/server:content/sync" ) ) ).getStatus() );
        verify( taskService, never() ).submitLocalTask( any() );
    }

    private SubmitLocalTaskParams submitted()
    {
        final ArgumentCaptor<SubmitLocalTaskParams> captor = ArgumentCaptor.forClass( SubmitLocalTaskParams.class );
        verify( taskService ).submitLocalTask( captor.capture() );
        return captor.getValue();
    }
}
