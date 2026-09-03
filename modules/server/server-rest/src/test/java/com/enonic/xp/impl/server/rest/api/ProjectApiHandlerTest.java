package com.enonic.xp.impl.server.rest.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectApiHandlerTest
{
    private ProjectService projectService;

    private ProjectApiHandler handler;

    @BeforeEach
    void setUp()
    {
        projectService = mock( ProjectService.class );
        handler = new ProjectApiHandler( projectService, mock( ContentService.class ) );
    }

    @Test
    void list()
    {
        when( projectService.list() ).thenReturn( Projects.create().build() );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:project" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "{\"projects\":[]}", response.getBody() );
    }
}
