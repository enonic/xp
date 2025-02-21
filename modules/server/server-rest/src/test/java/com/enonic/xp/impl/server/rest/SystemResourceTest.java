package com.enonic.xp.impl.server.rest;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.impl.server.rest.model.VacuumRequestJson;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SystemResourceTest
    extends JaxRsResourceTestSupport
{
    private TaskService taskService;

    private SystemResource resource;

    @Test
    void dump()
        throws Exception
    {
        when( taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "task-id" ) );

        final String result =
            request().path( "system/dump" ).entity( "{\"name\" : \"dump\"}", MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        final ArgumentCaptor<SubmitLocalTaskParams> captor = ArgumentCaptor.forClass( SubmitLocalTaskParams.class );

        verify( taskService, times( 1 ) ).submitLocalTask( captor.capture() );
        assertThat( captor.getValue() ).extracting( SubmitLocalTaskParams::getName, SubmitLocalTaskParams::getDescription )
            .containsExactly( "dump", "Dump dump" );

        assertEquals( "{\"taskId\":\"task-id\"}", result );
    }

    @Test
    void load()
        throws Exception
    {
        when( taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "task-id" ) );

        final String result =
            request().path( "system/load" ).entity( "{\"name\" : \"dump\"}", MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        final ArgumentCaptor<SubmitLocalTaskParams> captor = ArgumentCaptor.forClass( SubmitLocalTaskParams.class );

        verify( taskService, times( 1 ) ).submitLocalTask( captor.capture() );
        assertThat( captor.getValue() ).extracting( SubmitLocalTaskParams::getName, SubmitLocalTaskParams::getDescription )
            .containsExactly( "load", "Load dump" );

        assertEquals( "{\"taskId\":\"task-id\"}", result );
    }

    @Test
    void vacuum()
    {
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "task-id" ) );

        final TaskResultJson result = resource.vacuum( new VacuumRequestJson( null, null ) );
        assertEquals( "task-id", result.getTaskId() );
    }

    @Test
    void upgrade()
        throws Exception
    {
        when( taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "task-id" ) );

        final String result =
            request().path( "system/upgrade" ).entity( "{\"name\" : \"dump-name\"}", MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        final ArgumentCaptor<SubmitLocalTaskParams> captor = ArgumentCaptor.forClass( SubmitLocalTaskParams.class );

        verify( taskService, times( 1 ) ).submitLocalTask( captor.capture() );
        assertThat( captor.getValue() ).extracting( SubmitLocalTaskParams::getName,
                                                                         SubmitLocalTaskParams::getDescription )
            .containsExactly( null, "Upgrade dump dump-name" );

        assertEquals( "{\"taskId\":\"task-id\"}", result );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.taskService = mock( TaskService.class );

        resource = new SystemResource(null, taskService);

        return resource;
    }
}
