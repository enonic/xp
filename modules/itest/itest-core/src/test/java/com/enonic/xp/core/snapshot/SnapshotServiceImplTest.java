package com.enonic.xp.core.snapshot;

import java.util.stream.Collectors;

import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.elasticsearch.snapshot.SnapshotException;
import com.enonic.xp.repo.impl.elasticsearch.snapshot.SnapshotServiceImpl;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.repository.NodeRepositoryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryServiceImpl;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnapshotServiceImplTest
    extends AbstractNodeTest
{
    private SnapshotServiceImpl snapshotService;

    private RepositoryServiceImpl repositoryService;

    public SnapshotServiceImplTest()
    {
        super( true );
    }

    @BeforeEach
    void setUp()
    {
        for ( String repository : client.admin()
            .cluster()
            .prepareGetRepositories()
            .execute()
            .actionGet()
            .repositories()
            .stream()
            .map( RepositoryMetaData::name )
            .collect( Collectors.toList() ) )
        {
            for ( String snapshot : client.admin()
                .cluster()
                .prepareGetSnapshots( repository )
                .execute()
                .actionGet()
                .getSnapshots()
                .stream()
                .map( SnapshotInfo::name )
                .collect( Collectors.toList() ) )
            {
                client.admin().cluster().prepareDeleteSnapshot( repository, snapshot ).execute().actionGet();
            }
            client.admin().cluster().prepareDeleteRepository( repository ).execute().actionGet();
        }

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl( this.indexServiceInternal );

        this.repositoryService =
            new RepositoryServiceImpl( this.repositoryEntryService, this.indexServiceInternal, nodeRepositoryService, this.storageService,
                                       this.searchService );

        final RepoConfiguration configuration = Mockito.mock( RepoConfiguration.class );
        Mockito.when( configuration.getSnapshotsDir() ).thenReturn( getSnapshotsDir() );

        this.snapshotService =
            new SnapshotServiceImpl( client, configuration, repositoryEntryService, eventPublisher, indexServiceInternal );
    }

    @Test
    void non_builtin_repos_snapshotted()
    {
        NodeHelper.runAsAdmin( () -> {
            final RepositoryId newRepoId = RepositoryId.from( "new-repo" );
            this.repositoryService.createRepository( CreateRepositoryParams.create().repositoryId( newRepoId ).build() );

            this.snapshotService.snapshot( SnapshotParams.create().snapshotName( "my-snapshot" ).build() );

            this.repositoryService.deleteRepository( DeleteRepositoryParams.from( newRepoId ) );
            assertNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.restore( RestoreParams.create().snapshotName( "my-snapshot" ).build() );

            assertNotNull( this.repositoryService.get( newRepoId ) );
            assertNotNull( this.repositoryService.get( SystemConstants.SYSTEM_REPO_ID ) );
            assertNotNull( this.repositoryService.get( testRepoId ) );
        } );
    }

    @Test
    void repository_id_given()
    {
        NodeHelper.runAsAdmin( () -> {
            final RepositoryId newRepoId = RepositoryId.from( "new-repo" );
            this.repositoryService.createRepository( CreateRepositoryParams.create().repositoryId( newRepoId ).build() );

            assertNotNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.snapshot( SnapshotParams.create().snapshotName( "my-snapshot" ).build() );

            this.repositoryService.deleteRepository( DeleteRepositoryParams.from( newRepoId ) );
            assertNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.restore( RestoreParams.create().repositoryId( newRepoId ).snapshotName( "my-snapshot" ).build() );

            // The system repo does not now about this repo now
            assertNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.restore(
                RestoreParams.create().repositoryId( SystemConstants.SYSTEM_REPO_ID ).snapshotName( "my-snapshot" ).build() );

            assertTrue( this.repositoryService.isInitialized( newRepoId ) );
            assertTrue( this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO_ID ) );
            assertTrue( this.repositoryService.isInitialized( testRepoId ) );
        } );
    }

    @Test
    void restore_all()
    {
        NodeHelper.runAsAdmin( () -> {
            final RepositoryId newRepoId = RepositoryId.from( "new-repo" );
            this.repositoryService.createRepository( CreateRepositoryParams.create().repositoryId( newRepoId ).build() );

            assertNotNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.snapshot( SnapshotParams.create().snapshotName( "my-snapshot" ).build() );

            this.repositoryService.deleteRepository( DeleteRepositoryParams.from( newRepoId ) );
            assertNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.restore( RestoreParams.create().snapshotName( "my-snapshot" ).build() );

            assertTrue( this.repositoryService.isInitialized( newRepoId ) );
            assertTrue( this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO_ID ) );
            assertTrue( this.repositoryService.isInitialized( testRepoId ) );
        } );
    }

    @Test
    void restore_all_with_deletion()
    {
        NodeHelper.runAsAdmin( () -> {
            final RepositoryId newRepoId = RepositoryId.from( "new-repo" );
            this.repositoryService.createRepository( CreateRepositoryParams.create().repositoryId( newRepoId ).build() );

            assertNotNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.snapshot( SnapshotParams.create().snapshotName( "my-snapshot" ).build() );

            this.repositoryService.deleteRepository( DeleteRepositoryParams.from( newRepoId ) );
            assertNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.restore( RestoreParams.create().snapshotName( "my-snapshot" ).force( true ).build() );

            assertTrue( this.repositoryService.isInitialized( newRepoId ) );
            assertTrue( this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO_ID ) );
            assertTrue( this.repositoryService.isInitialized( testRepoId ) );
        } );
    }

    @Test
    void restore_single_with_deletion()
    {
        NodeHelper.runAsAdmin( () -> {
            final RepositoryId newRepoId = RepositoryId.from( "new-repo-1" );

            this.repositoryService.createRepository( CreateRepositoryParams.create().repositoryId( newRepoId ).build() );

            final RepositoryId newRepoId2 = RepositoryId.from( "new-repo-2" );
            this.repositoryService.createRepository( CreateRepositoryParams.create().repositoryId( newRepoId2 ).build() );

            this.snapshotService.snapshot( SnapshotParams.create().snapshotName( "my-snapshot" ).build() );

            this.repositoryService.deleteRepository( DeleteRepositoryParams.from( newRepoId ) );
            assertNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.restore(
                RestoreParams.create().repositoryId( newRepoId ).snapshotName( "my-snapshot" ).force( true ).build() );

            assertTrue( this.repositoryService.isInitialized( newRepoId2 ) );

            this.snapshotService.restore(
                RestoreParams.create().repositoryId( SystemConstants.SYSTEM_REPO_ID ).snapshotName( "my-snapshot" ).force( true ).build() );

            assertTrue( this.repositoryService.isInitialized( newRepoId ) );
            assertTrue( this.repositoryService.isInitialized( newRepoId2 ) );
            assertTrue( this.repositoryService.isInitialized( SystemConstants.SYSTEM_REPO_ID ) );
            assertTrue( this.repositoryService.isInitialized( testRepoId ) );
        } );
    }

    @Test
    void restore_all_no_orphan_indices_left()
    {
        NodeHelper.runAsAdmin( () -> {
            this.snapshotService.snapshot( SnapshotParams.create().snapshotName( "my-snapshot" ).build() );

            final RepositoryId newRepoId = RepositoryId.from( "new-repo" );
            this.repositoryService.createRepository( CreateRepositoryParams.create().repositoryId( newRepoId ).build() );

            assertNotNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.restore( RestoreParams.create().snapshotName( "my-snapshot" ).build() );

            assertAll( () -> assertFalse( this.repositoryService.isInitialized( newRepoId ) ),
                       () -> assertFalse( indexServiceInternal.indicesExists( IndexNameResolver.resolveStorageIndexName( newRepoId ) ) ),
                       () -> assertFalse( indexServiceInternal.indicesExists( IndexNameResolver.resolveSearchIndexName( newRepoId ) ) ) );
        } );
    }

    @Test
    void restore_all_no_transient_left()
    {
        NodeHelper.runAsAdmin( () -> {
            // transient repositories are not part of snapshot - there should be no record in system repo after restore
            final RepositoryId newRepoId = RepositoryId.from( "transient-repo" );
            this.repositoryService.createRepository(
                CreateRepositoryParams.create().repositoryId( newRepoId ).transientFlag( true ).build() );

            assertNotNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.snapshot( SnapshotParams.create().snapshotName( "my-snapshot" ).build() );

            // transient repositories are not in snapshot - their indices must be deleted after restore
            final RepositoryId newRepoAfterSnapshotId = RepositoryId.from( "transient-repo-after-snapshot" );
            this.repositoryService.createRepository(
                CreateRepositoryParams.create().repositoryId( newRepoAfterSnapshotId ).transientFlag( true ).build() );

            this.snapshotService.restore( RestoreParams.create().snapshotName( "my-snapshot" ).build() );

            assertAll( () -> assertNull( this.repositoryEntryService.getRepositoryEntry( newRepoId ) ),
                       () -> assertFalse( indexServiceInternal.indicesExists( IndexNameResolver.resolveStorageIndexName( newRepoId ) ) ),
                       () -> assertFalse( indexServiceInternal.indicesExists( IndexNameResolver.resolveSearchIndexName( newRepoId ) ) ) );
            assertAll( () -> assertFalse( this.repositoryService.isInitialized( newRepoAfterSnapshotId ) ),
                       () -> assertFalse( indexServiceInternal.indicesExists( IndexNameResolver.resolveStorageIndexName( newRepoAfterSnapshotId ) ) ),
                       () -> assertFalse( indexServiceInternal.indicesExists( IndexNameResolver.resolveSearchIndexName( newRepoAfterSnapshotId ) ) ) );
        } );
    }

    @Test
    void restore_invalid_snapshot()
    {
        assertThrows( SnapshotException.class, () -> NodeHelper.runAsAdmin( () -> {
            final RepositoryId newRepoId = RepositoryId.from( "new-repo" );
            this.repositoryService.createRepository( CreateRepositoryParams.create().repositoryId( newRepoId ).build() );

            this.snapshotService.snapshot( SnapshotParams.create().snapshotName( "my-snapshot" ).build() );

            this.snapshotService.delete( DeleteSnapshotParams.create().add( "my-snapshot" ).build() );

            this.repositoryService.deleteRepository( DeleteRepositoryParams.from( newRepoId ) );

            try
            {
                this.snapshotService.restore( RestoreParams.create().snapshotName( "my-snapshot" ).build() );
            }
            finally
            {
                assertNotNull( this.repositoryService.get( testRepoId ) );
            }
        } ) );
    }

    @Test
    void restore_latest()
    {
        NodeHelper.runAsAdmin( () -> {
            final RepositoryId newRepoId = RepositoryId.from( "new-repo" );
            this.repositoryService.createRepository( CreateRepositoryParams.create().repositoryId( newRepoId ).build() );

            assertNotNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.snapshot( SnapshotParams.create().snapshotName( "my-snapshot-latest" ).build() );

            this.repositoryService.deleteRepository( DeleteRepositoryParams.from( newRepoId ) );
            assertNull( this.repositoryService.get( newRepoId ) );

            final RestoreResult restoreResult =
                this.snapshotService.restore( RestoreParams.create().repositoryId( newRepoId ).latest( true ).build() );

            assertEquals( "my-snapshot-latest", restoreResult.getName() );

            // The system repo does not know about this repo now
            assertNull( this.repositoryService.get( newRepoId ) );

            this.snapshotService.restore(
                RestoreParams.create().repositoryId( SystemConstants.SYSTEM_REPO_ID ).snapshotName( "my-snapshot-latest" ).build() );

            assertNotNull( this.repositoryService.get( newRepoId ) );
            assertNotNull( this.repositoryService.get( SystemConstants.SYSTEM_REPO_ID ) );
            assertNotNull( this.repositoryService.get( testRepoId ) );
        } );
    }

    @Test
    void restore_latest_no_snapshots()
    {
        final SnapshotException exception =
            assertThrows( SnapshotException.class, () -> snapshotService.restore( RestoreParams.create().latest( true ).build() ) );

        assertEquals( "No snapshots found", exception.getMessage() );
    }

    @Test
    void list_snapshots()
    {
        NodeHelper.runAsAdmin( () -> {
            this.snapshotService.snapshot( SnapshotParams.create().snapshotName( "my-snapshot" ).build() );

            this.snapshotService.snapshot( SnapshotParams.create().snapshotName( "my-snapshot2" ).build() );

            final SnapshotResults result = this.snapshotService.list();

            assertEquals( 2, result.getSize() );
        } );
    }
}
