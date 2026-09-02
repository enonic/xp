package com.enonic.xp.impl.server.rest.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectApiHandlerTest
{
    private ProjectService projectService;

    private TaskService taskService;

    private ProjectApiHandler handler;

    @BeforeEach
    void setUp()
    {
        projectService = mock( ProjectService.class );
        taskService = mock( TaskService.class );
        handler = new ProjectApiHandler( projectService, mock( ContentService.class ), taskService );
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "t1" ) );
    }

    @Test
    void list()
    {
        when( projectService.list() ).thenReturn( Projects.create().build() );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:project" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "{\"projects\":[]}", response.getBody() );
    }

    @Test
    void syncAll()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/server:project/sync" ) );

        assertEquals( HttpStatus.ACCEPTED, response.getStatus() );
        final SubmitTaskParams params = submitted();
        assertEquals( "com.enonic.xp.app.system:project-sync", params.getDescriptorKey().toString() );
        assertTrue( Lists.newArrayList( params.getData().getStrings( "projects" ) ).isEmpty() );
    }

    @Test
    void syncNamed()
    {
        handler.handle( request( HttpMethod.POST, "/server:project/sync", "{\"projects\":[\"intranet\"]}" ) );

        assertEquals( List.of( "intranet" ), Lists.newArrayList( submitted().getData().getStrings( "projects" ) ) );
    }

    @Test
    void syncRejectsInvalidProjectName()
    {
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:project/sync", "{\"projects\":[\"Not A Project\"]}" ) ).getStatus() );
    }

    private SubmitTaskParams submitted()
    {
        final ArgumentCaptor<SubmitTaskParams> captor = ArgumentCaptor.forClass( SubmitTaskParams.class );
        verify( taskService ).submitTask( captor.capture() );
        return captor.getValue();
    }
}
