package com.enonic.xp.impl.server.rest.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.task.SubmitLocalTaskParams;
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
        when( taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "t1" ) );
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
    void loadAndUpgradeTakeNameFromPath()
    {
        assertEquals( HttpStatus.ACCEPTED, handler.handle( request( HttpMethod.POST, "/server:dump/full/load" ) ).getStatus() );
        assertEquals( HttpStatus.ACCEPTED, handler.handle( request( HttpMethod.POST, "/server:dump/full/upgrade" ) ).getStatus() );

        final ArgumentCaptor<SubmitLocalTaskParams> captor = ArgumentCaptor.forClass( SubmitLocalTaskParams.class );
        verify( taskService, org.mockito.Mockito.times( 2 ) ).submitLocalTask( captor.capture() );
        assertEquals( "Load full", captor.getAllValues().get( 0 ).getDescription() );
        assertEquals( "Upgrade dump full", captor.getAllValues().get( 1 ).getDescription() );
    }
}
