package com.enonic.xp.impl.server.rest;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContentResourceTest
    extends JaxRsResourceTestSupport
{

    private TaskService taskService;

    @Test
    void sync()
        throws Exception
    {
        when( this.taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "task-id" ) );

        final MockRestResponse result = request().path( "content/syncAll" ).
            entity( "", MediaType.APPLICATION_JSON_TYPE ).
            post();

        ArgumentCaptor<SubmitLocalTaskParams> submitLocalTaskParamsCaptor = ArgumentCaptor.forClass( SubmitLocalTaskParams.class );
        verify( taskService, times( 1 ) ).submitLocalTask( submitLocalTaskParamsCaptor.capture() );
        assertThat( submitLocalTaskParamsCaptor.getValue() ).extracting( SubmitLocalTaskParams::getName,
                                                                         SubmitLocalTaskParams::getDescription )
            .containsExactly( "sync-all-projects", "Sync all projects" );

        assertEquals( "{\"taskId\":\"task-id\"}", result.getDataAsString() );
    }

    @Override
    protected Object getResourceInstance()
    {
        ContentService contentService = mock( ContentService.class );
        this.taskService = mock( TaskService.class );

        final ProjectService projectService = mock( ProjectService.class );
        final SyncContentService syncContentService = mock( SyncContentService.class );

        final ContentResource resource = new ContentResource();
        resource.setContentService( contentService );
        resource.setTaskService( taskService );
        resource.setProjectService( projectService );
        resource.setSyncContentService( syncContentService );
        return resource;
    }
}
