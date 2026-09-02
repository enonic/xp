package com.enonic.xp.impl.server.rest;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.task.SubmitTaskParams;
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
        when( this.taskService.submitTask( any() ) ).thenReturn( TaskId.from( "task-id" ) );

        final MockRestResponse result = request().path( "content/syncAll" ).
            entity( "", MediaType.APPLICATION_JSON_TYPE ).
            post();

        ArgumentCaptor<SubmitTaskParams> submitTaskParamsCaptor = ArgumentCaptor.forClass( SubmitTaskParams.class );
        verify( taskService, times( 1 ) ).submitTask( submitTaskParamsCaptor.capture() );
        assertThat( submitTaskParamsCaptor.getValue().getDescriptorKey().toString() ).isEqualTo( "com.enonic.xp.app.system:project-sync" );

        assertEquals( "{\"taskId\":\"task-id\"}", result.getDataAsString() );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.taskService = mock( TaskService.class );

        final ContentResource resource = new ContentResource();
        resource.setTaskService( taskService );
        return resource;
    }
}
