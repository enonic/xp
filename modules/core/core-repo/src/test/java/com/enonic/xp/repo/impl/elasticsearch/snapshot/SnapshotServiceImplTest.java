package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.elasticsearch.snapshots.SnapshotState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.repository.RepositoryEntryService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SnapshotServiceImplTest
{
    private ClusterAdminClient clusterAdminClient;

    private SnapshotServiceImpl instance;

    @BeforeEach
    void setUp()
    {
        final Client client = mock( Client.class );
        clusterAdminClient = mock( ClusterAdminClient.class );

        when( client.admin() ).thenReturn( mock( AdminClient.class ) );
        when( client.admin().cluster() ).thenReturn( clusterAdminClient );

        final RepoConfiguration configuration = mock( RepoConfiguration.class );
        when( configuration.getSnapshotsDir() ).thenReturn( Path.of( "tmp", "data" ) );

        final RepositoryEntryService repositoryEntryService = mock( RepositoryEntryService.class );
        final EventPublisher eventPublisher = mock( EventPublisher.class );
        final IndexServiceInternal indexServiceInternal = mock( IndexServiceInternal.class );

        instance = new SnapshotServiceImpl( client, configuration, repositoryEntryService, eventPublisher, indexServiceInternal );
    }

    @Test
    void testDelete()
    {
        final RepositoryMetaData repositoryMetaData = new RepositoryMetaData( "enonic-xp-snapshot-repo", "fs", Settings.settingsBuilder()
            .put( "compress", true )
            .put( "location", Path.of( "tmp", "data" ).toString() )
            .build() );

        final GetRepositoriesResponse getRepositoriesResponse = mock( GetRepositoriesResponse.class );
        when( getRepositoriesResponse.repositories() ).thenReturn( List.of( repositoryMetaData ) );

        final ActionFuture<GetRepositoriesResponse> getRepositoryActionFuture = mock( ActionFuture.class );
        when( getRepositoryActionFuture.actionGet() ).thenReturn( getRepositoriesResponse );

        when( clusterAdminClient.getRepositories( any( GetRepositoriesRequest.class ) ) ).thenReturn( getRepositoryActionFuture );

        final Instant now = Instant.now();

        final SnapshotInfo snapshot3 = mockSnapshot( "snapshot3", SnapshotState.SUCCESS, now.minus( 3, ChronoUnit.HOURS ) );
        final SnapshotInfo snapshot4 = mockSnapshot( "snapshot4", SnapshotState.PARTIAL, now.minus( 4, ChronoUnit.HOURS ) );
        final SnapshotInfo snapshot5 = mockSnapshot( "snapshot5", SnapshotState.SUCCESS, now.minus( 1, ChronoUnit.HOURS ) );

        final GetSnapshotsResponse getSnapshotsResponse = mock( GetSnapshotsResponse.class );
        when( getSnapshotsResponse.getSnapshots() ).thenReturn( List.of( snapshot3, snapshot4, snapshot5 ) );

        final ActionFuture<GetSnapshotsResponse> getSnapshotsActionFuture = mock( ActionFuture.class );
        when( getSnapshotsActionFuture.actionGet() ).thenReturn( getSnapshotsResponse );

        when( clusterAdminClient.getSnapshots( any( GetSnapshotsRequest.class ) ) ).thenReturn( getSnapshotsActionFuture );

        when( clusterAdminClient.deleteSnapshot( any( DeleteSnapshotRequest.class ) ) ).thenReturn( mock( ActionFuture.class ) );
        when( clusterAdminClient.deleteSnapshot(
            argThat( request -> "snapshot2".equals( request.snapshot() ) || "snapshot4".equals( request.snapshot() ) ) ) ).thenThrow(
            new ElasticsearchException( "Failed to delete snapshot" ) );

        final DeleteSnapshotsResult result = instance.delete(
            DeleteSnapshotParams.create().add( "snapshot1" ).add( "snapshot2" ).before( now.minus( 2, ChronoUnit.HOURS ) ).build() );

        assertEquals( 2, result.getDeletedSnapshots().size() );
        assertTrue( result.getDeletedSnapshots().contains( "snapshot1" ) );
        assertTrue( result.getDeletedSnapshots().contains( "snapshot3" ) );
        assertEquals( 2, result.getFailedSnapshots().size() );
        assertTrue( result.getFailedSnapshots().contains( "snapshot2" ) );
        assertTrue( result.getFailedSnapshots().contains( "snapshot4" ) );
    }

    private static SnapshotInfo mockSnapshot( String name, SnapshotState state, Instant endTime )
    {
        final SnapshotInfo snapshot = mock( SnapshotInfo.class );

        when( snapshot.state() ).thenReturn( state );
        when( snapshot.name() ).thenReturn( name );
        when( snapshot.endTime() ).thenReturn( endTime.toEpochMilli() );

        return snapshot;
    }
}
