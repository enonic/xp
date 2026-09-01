package com.enonic.xp.impl.server.rest.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.export.ExportInfo;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ListExportsResult;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExportApiHandlerTest
{
    private ExportService exportService;

    private TaskService taskService;

    private ExportApiHandler handler;

    @BeforeEach
    void setUp()
    {
        exportService = mock( ExportService.class );
        taskService = mock( TaskService.class );
        handler = new ExportApiHandler( exportService, taskService );
        when( taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "t1" ) );
    }

    @Test
    void list()
    {
        when( exportService.list() ).thenReturn( ListExportsResult.create().addExport( new ExportInfo( "nightly" ) ).build() );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:export" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "{\"exports\":[{\"name\":\"nightly\"}]}", response.getBody() );
    }

    @Test
    void create()
    {
        final WebResponse response = handler.handle(
            request( HttpMethod.POST, "/server:export", "{\"sourceRepoPath\":\"com.enonic.cms.default:draft:/\",\"exportName\":\"nightly\"}" ) );

        assertEquals( HttpStatus.ACCEPTED, response.getStatus() );
        assertEquals( "{\"taskId\":\"t1\"}", response.getBody() );

        final ArgumentCaptor<SubmitLocalTaskParams> captor = ArgumentCaptor.forClass( SubmitLocalTaskParams.class );
        verify( taskService ).submitLocalTask( captor.capture() );
        assertEquals( "Export nightly", captor.getValue().getDescription() );
    }

    @Test
    void createRequiresNameAndPath()
    {
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:export", "{\"exportName\":\"x\"}" ) ).getStatus() );
        verify( taskService, never() ).submitLocalTask( any() );
    }

    @Test
    void loadTakesNameFromPath()
    {
        final WebResponse response = handler.handle(
            request( HttpMethod.POST, "/server:export/nightly/load", "{\"targetRepoPath\":\"com.enonic.cms.default:draft:/\",\"importWithIds\":true}" ) );

        assertEquals( HttpStatus.ACCEPTED, response.getStatus() );

        final ArgumentCaptor<SubmitLocalTaskParams> captor = ArgumentCaptor.forClass( SubmitLocalTaskParams.class );
        verify( taskService ).submitLocalTask( captor.capture() );
        assertEquals( "Import nightly", captor.getValue().getDescription() );
    }
}
