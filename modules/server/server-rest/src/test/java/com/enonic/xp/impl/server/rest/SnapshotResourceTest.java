package com.enonic.xp.impl.server.rest;

import java.time.Instant;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SnapshotResourceTest
    extends JaxRsResourceTestSupport
{
    private SnapshotService snapshotService;

    private TaskService taskService;

    @Test
    void snapshot()
        throws Exception
    {
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "task-id-1" ) );

        final String result = request().path( "repo/snapshot" )
            .entity( readFromFile( "snapshot_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertStringJson( "{\"taskId\" : \"task-id-1\"}", result );
    }

    @Test
    void restore()
        throws Exception
    {
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "task-id-2" ) );

        final String result = request().path( "repo/snapshot/restore" )
            .entity( readFromFile( "restore_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertStringJson( "{\"taskId\" : \"task-id-2\"}", result );
    }

    @Test
    void restore_with_deletion()
        throws Exception
    {
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "task-id-3" ) );

        final String result = request().path( "repo/snapshot/restore" )
            .entity( readFromFile( "restore_with_deletion_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertStringJson( "{\"taskId\" : \"task-id-3\"}", result );
    }

    @Test
    void restore_latest()
        throws Exception
    {
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "task-id-4" ) );

        final String result = request().path( "repo/snapshot/restore" )
            .entity( readFromFile( "restore_latest_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertStringJson( "{\"taskId\" : \"task-id-4\"}", result );
    }

    @Test
    void delete()
        throws Exception
    {
        final DeleteSnapshotsResult deleteResult = DeleteSnapshotsResult.create().add( "snapshot1" ).build();

        Mockito.when( this.snapshotService.delete( isA( DeleteSnapshotParams.class ) ) ).thenReturn( deleteResult );

        final String result = request().path( "repo/snapshot/delete" )
            .entity( readFromFile( "delete_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertJson( "delete.json", result );
    }

    @Test
    void list()
        throws Exception
    {
        final SnapshotResult snapshotResult1 = SnapshotResult.create()
            .name( "name1" )
            .reason( "because reasons" )
            .timestamp( Instant.ofEpochMilli( 1438866915875L ) )
            .state( SnapshotResult.State.SUCCESS )
            .indices( Arrays.asList( "ac27b1" ) )
            .build();

        final SnapshotResults snapshotResults = SnapshotResults.create().add( snapshotResult1 ).build();

        Mockito.when( this.snapshotService.list() ).thenReturn( snapshotResults );

        String result = request().path( "repo/snapshot/list" ).get().getAsString();

        assertJson( "list.json", result );
    }


    @Override
    protected Object getResourceInstance()
    {
        this.snapshotService = mock( SnapshotService.class );
        this.taskService = mock( TaskService.class );

        return new SnapshotResource( snapshotService, taskService );
    }
}
