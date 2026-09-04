package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.withVirtualHostContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SnapshotApiHandlerTest
{
    private SnapshotService snapshotService;

    private TaskService taskService;

    private SnapshotApiHandler handler;

    @BeforeEach
    void setUp()
    {
        snapshotService = mock( SnapshotService.class );
        taskService = mock( TaskService.class );
        handler = new SnapshotApiHandler( snapshotService, taskService );
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "t1" ) );
    }

    @Test
    void list()
    {
        when( snapshotService.list() ).thenReturn( SnapshotResults.create().build() );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:snapshot" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "{\"results\":[]}", response.getBody() );
    }

    @Test
    void createWithoutBody()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/server:snapshot" ) );

        assertEquals( HttpStatus.ACCEPTED, response.getStatus() );
        final SubmitTaskParams params = submitted();
        assertEquals( "com.enonic.xp.app.system:snapshot", params.getDescriptorKey().toString() );
        assertNull( params.getData().getString( "snapshotName" ) );
    }

    @Test
    void createNamed()
    {
        handler.handle( request( HttpMethod.POST, "/server:snapshot", "{\"snapshotName\":\"nightly\",\"repositoryId\":\"a\"}" ) );

        final SubmitTaskParams params = submitted();
        assertEquals( "nightly", params.getData().getString( "snapshotName" ) );
        assertEquals( "a", params.getData().getString( "repositoryId" ) );
    }

    @Test
    void restoreNamed()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/server:snapshot/nightly/restore", "{\"force\":true}" ) );

        assertEquals( HttpStatus.ACCEPTED, response.getStatus() );
        final SubmitTaskParams params = submitted();
        assertEquals( "com.enonic.xp.app.system:restore", params.getDescriptorKey().toString() );
        assertEquals( "nightly", params.getData().getString( "snapshotName" ) );
        assertEquals( Boolean.FALSE, params.getData().getBoolean( "latest" ) );
        assertEquals( Boolean.TRUE, params.getData().getBoolean( "force" ) );
    }

    @Test
    void restoreLatest()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/server:snapshot/restore", "{\"latest\":true}" ) );

        assertEquals( HttpStatus.ACCEPTED, response.getStatus() );
        final SubmitTaskParams params = submitted();
        assertNull( params.getData().getString( "snapshotName" ) );
        assertEquals( Boolean.TRUE, params.getData().getBoolean( "latest" ) );
    }

    @Test
    void restoreWithoutNameOrLatest()
    {
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:snapshot/restore", "{}" ) ).getStatus() );
        verify( taskService, never() ).submitTask( any() );
    }

    @Test
    void pruneNamed()
    {
        when( snapshotService.delete( any() ) ).thenReturn( DeleteSnapshotsResult.create().add( "nightly" ).build() );

        final WebResponse response = handler.handle( request( HttpMethod.DELETE, "/server:snapshot/nightly" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        final ArgumentCaptor<DeleteSnapshotParams> captor = ArgumentCaptor.forClass( DeleteSnapshotParams.class );
        verify( snapshotService ).delete( captor.capture() );
        assertTrue( captor.getValue().getSnapshotNames().contains( "nightly" ) );
        assertNull( captor.getValue().getBefore() );
    }

    @Test
    void pruneBefore()
    {
        when( snapshotService.delete( any() ) ).thenReturn( DeleteSnapshotsResult.create().build() );

        final WebRequest request = request( HttpMethod.DELETE, "/server:snapshot" );
        request.getParams().put( "before", "2026-01-01T00:00:00Z" );

        assertEquals( HttpStatus.OK, handler.handle( request ).getStatus() );

        final ArgumentCaptor<DeleteSnapshotParams> captor = ArgumentCaptor.forClass( DeleteSnapshotParams.class );
        verify( snapshotService ).delete( captor.capture() );
        assertNotNull( captor.getValue().getBefore() );
        assertTrue( captor.getValue().getSnapshotNames().isEmpty() );
    }

    @Test
    void pruneWithoutNameOrBefore()
    {
        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.DELETE, "/server:snapshot" ) ).getStatus() );
        verify( snapshotService, never() ).delete( any() );
    }

    @Test
    void backupVhostCannotRestoreOrPrune()
    {
        final Map<String, String> policy = Map.of( "api.server:snapshot.verbs", "list, create" );

        assertEquals( HttpStatus.ACCEPTED, withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.POST, "/server:snapshot" ) ) ).getStatus() );
        assertEquals( HttpStatus.FORBIDDEN,
                      withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.POST, "/server:snapshot/nightly/restore" ) ) ).getStatus() );
        assertEquals( HttpStatus.FORBIDDEN,
                      withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.DELETE, "/server:snapshot/nightly" ) ) ).getStatus() );
        verify( snapshotService, never() ).delete( any() );
    }

    private SubmitTaskParams submitted()
    {
        final ArgumentCaptor<SubmitTaskParams> captor = ArgumentCaptor.forClass( SubmitTaskParams.class );
        verify( taskService ).submitTask( captor.capture() );
        return captor.getValue();
    }
}
