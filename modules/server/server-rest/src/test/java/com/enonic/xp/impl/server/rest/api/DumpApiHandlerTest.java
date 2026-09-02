package com.enonic.xp.impl.server.rest.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DumpApiHandlerTest
{
    private DumpService dumpService;

    private TaskService taskService;

    private DumpApiHandler handler;

    @BeforeEach
    void setUp()
    {
        dumpService = mock( DumpService.class );
        taskService = mock( TaskService.class );
        handler = new DumpApiHandler( dumpService, taskService );
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "t1" ) );
    }

    @Test
    void list()
    {
        when( dumpService.list() ).thenReturn( List.of() );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:dump" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "{\"dumps\":[]}", response.getBody() );
    }

    @Test
    void createRequiresName()
    {
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:dump", "{}" ) ).getStatus() );
    }

    @Test
    void create()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/server:dump", "{\"name\":\"full\",\"includeVersions\":true}" ) );

        assertEquals( HttpStatus.ACCEPTED, response.getStatus() );
        assertEquals( "{\"taskId\":\"t1\"}", response.getBody() );
    }

    @Test
    void createPassesParams()
    {
        handler.handle( request( HttpMethod.POST, "/server:dump", "{\"name\":\"full\",\"includeVersions\":true,\"maxAge\":30,\"repositories\":[\"a\"]}" ) );

        final SubmitTaskParams params = submitted( 1 ).get( 0 );
        assertEquals( "com.enonic.xp.app.system:dump", params.getDescriptorKey().toString() );
        assertEquals( "full", params.getData().getString( "name" ) );
        assertEquals( Boolean.TRUE, params.getData().getBoolean( "includeVersions" ) );
        assertEquals( 30L, params.getData().getLong( "maxAge" ) );
        assertEquals( "a", params.getData().getString( "repositories" ) );
    }

    @Test
    void loadAndUpgradeTakeNameFromPath()
    {
        assertEquals( HttpStatus.ACCEPTED, handler.handle( request( HttpMethod.POST, "/server:dump/full/load", "{\"upgrade\":true}" ) ).getStatus() );
        assertEquals( HttpStatus.ACCEPTED, handler.handle( request( HttpMethod.POST, "/server:dump/full/upgrade" ) ).getStatus() );

        final List<SubmitTaskParams> params = submitted( 2 );
        assertEquals( "com.enonic.xp.app.system:load", params.get( 0 ).getDescriptorKey().toString() );
        assertEquals( "full", params.get( 0 ).getData().getString( "name" ) );
        assertEquals( Boolean.TRUE, params.get( 0 ).getData().getBoolean( "upgrade" ) );
        assertEquals( "com.enonic.xp.app.system:upgrade", params.get( 1 ).getDescriptorKey().toString() );
        assertEquals( "full", params.get( 1 ).getData().getString( "name" ) );
    }

    private List<SubmitTaskParams> submitted( final int times )
    {
        final ArgumentCaptor<SubmitTaskParams> captor = ArgumentCaptor.forClass( SubmitTaskParams.class );
        verify( taskService, org.mockito.Mockito.times( times ) ).submitTask( captor.capture() );
        return captor.getAllValues();
    }
}
