package com.enonic.xp.impl.server.rest;

import java.time.Instant;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.snapshot.SnapshotService;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;

public class SnapshotResourceTest
    extends JaxRsResourceTestSupport
{
    private SnapshotService snapshotService;

    @Test
    public void snapshot()
        throws Exception
    {

        final SnapshotResult snapshotResult = SnapshotResult.create()
            .name( "name" )
            .reason( "because reasons" )
            .timestamp( Instant.ofEpochMilli( 1438866915875L ) )
            .state( SnapshotResult.State.SUCCESS )
            .indices( Arrays.asList( "46f2a9" ) )
            .build();

        Mockito.when( this.snapshotService.snapshot( isA( SnapshotParams.class ) ) ).thenReturn( snapshotResult );

        final String result = request().path( "repo/snapshot" )
            .entity( readFromFile( "snapshot_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertJson( "snapshot.json", result );
    }

    @Test
    public void restore()
        throws Exception
    {
        final RestoreResult restoreResult = RestoreResult.create()
            .repositoryId( RepositoryId.from( "repo-id" ) )
            .name( "name" )
            .message( "He's dead, Jim." )
            .indices( Arrays.asList( "bc02aa" ) )
            .failed( false )
            .build();

        Mockito.when( this.snapshotService.restore( isA( RestoreParams.class ) ) ).thenReturn( restoreResult );

        final String result = request().path( "repo/snapshot/restore" )
            .entity( readFromFile( "restore_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertJson( "restore.json", result );
    }

    @Test
    public void restore_with_deletion()
        throws Exception
    {
        final RestoreResult restoreResult = RestoreResult.create()
            .repositoryId( RepositoryId.from( "repo-id" ) )
            .name( "name" )
            .message( "He's dead, Jim." )
            .indices( Arrays.asList( "bc02aa" ) )
            .failed( false )
            .build();

        Mockito.when( this.snapshotService.restore( isA( RestoreParams.class ) ) ).thenAnswer( ( params ) -> {
            Assertions.assertTrue( ( (RestoreParams) params.getArgument( 0 ) ).isForce() );
            return restoreResult;
        } );

        final String result = request().path( "repo/snapshot/restore" )
            .entity( readFromFile( "restore_with_deletion_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertJson( "restore.json", result );
    }

    @Test
    public void restore_latest()
        throws Exception
    {
        final RestoreResult restoreResult = RestoreResult.create()
            .repositoryId( RepositoryId.from( "repo-id" ) )
            .name( "name" )
            .message( "He's dead, Jim." )
            .indices( Arrays.asList( "bc02aa" ) )
            .failed( false )
            .build();

        Mockito.when( this.snapshotService.restore( isA( RestoreParams.class ) ) ).thenReturn( restoreResult );

        final String result = request().path( "repo/snapshot/restore" )
            .entity( readFromFile( "restore_latest_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertJson( "restore.json", result );
    }

    @Test
    public void delete()
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
    public void list()
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

        final SnapshotResource resource = new SnapshotResource();
        resource.setSnapshotService( snapshotService );
        return resource;
    }
}
