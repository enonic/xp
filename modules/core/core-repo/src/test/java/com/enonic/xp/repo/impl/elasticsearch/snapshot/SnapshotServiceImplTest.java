package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.io.IOException;
import java.util.stream.Collectors;

import org.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.cluster.ClusterManager;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.repository.NodeRepositoryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryServiceImpl;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SnapshotServiceImplTest
    extends AbstractNodeTest
{

    private SnapshotServiceImpl snapshotService;

    private RepositoryServiceImpl repositoryService;

    private EventPublisher eventPublisher;

    private ClusterManager clusterManager;

    @AfterAll
    public static void destroy()
        throws IOException
    {
        cleanRepositories();
    }

    @BeforeEach
    public void setUp()
        throws Exception
    {
        cleanRepositories();

        this.snapshotService = new SnapshotServiceImpl();

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl();
        nodeRepositoryService.setIndexServiceInternal( this.indexServiceInternal );

        final IndexServiceImpl indexService = new IndexServiceImpl();
        indexService.setIndexServiceInternal( this.indexServiceInternal );
        indexService.setRepositoryEntryService( repositoryEntryService );

        this.repositoryService = new RepositoryServiceImpl();
        repositoryService.setIndexServiceInternal( this.indexServiceInternal );
        repositoryService.setNodeStorageService( this.storageService );
        repositoryService.setNodeSearchService( this.searchService );
        repositoryService.setNodeRepositoryService( nodeRepositoryService );
        repositoryService.setIndexService( indexService );
        repositoryService.setRepositoryEntryService( this.repositoryEntryService );

        eventPublisher = Mockito.mock( EventPublisher.class );

        clusterManager = Mockito.mock( ClusterManager.class );
        Mockito.when( clusterManager.isHealthy() ).thenReturn( false );

        this.snapshotService.setRepositoryService( repositoryService );
        final RepoConfiguration configuration = Mockito.mock( RepoConfiguration.class );
        Mockito.when( configuration.getSnapshotsDir() ).thenReturn( getSnapshotsDir() );

        this.snapshotService.setConfiguration( configuration );
        this.snapshotService.setClient( client );
        this.snapshotService.setEventPublisher( eventPublisher );
        this.snapshotService.setClusterManager( clusterManager );
    }

    @Test
    public void non_builtin_repos_snapshotted()
        throws Exception
    {
        NodeHelper.runAsAdmin( this::doNonBuiltinReposSnapshotted );
    }

    private void doNonBuiltinReposSnapshotted()
    {
        final RepositoryId newRepoId = RepositoryId.from( "new-repo" );
        this.repositoryService.createRepository( CreateRepositoryParams.create().
            repositoryId( newRepoId ).
            build() );

        this.snapshotService.snapshot( SnapshotParams.create().
            snapshotName( "my-snapshot" ).
            build() );

        this.repositoryService.deleteRepository( DeleteRepositoryParams.from( newRepoId ) );
        assertNull( this.repositoryService.get( newRepoId ) );

        this.snapshotService.restore( RestoreParams.create().
            snapshotName( "my-snapshot" ).
            build() );

        assertNotNull( this.repositoryService.get( newRepoId ) );
        assertNotNull( this.repositoryService.get( SystemConstants.SYSTEM_REPO.getId() ) );
        assertNotNull( this.repositoryService.get( ContentConstants.CONTENT_REPO.getId() ) );
    }

    @Test
    public void repository_id_given()
        throws Exception
    {
        NodeHelper.runAsAdmin( this::doRepositoryIdGiven );
    }

    private void doRepositoryIdGiven()
    {
        final RepositoryId newRepoId = RepositoryId.from( "new-repo" );
        this.repositoryService.createRepository( CreateRepositoryParams.create().
            repositoryId( newRepoId ).
            build() );

        assertNotNull( this.repositoryService.get( newRepoId ) );

        this.snapshotService.snapshot( SnapshotParams.create().
            snapshotName( "my-snapshot" ).
            build() );

        this.repositoryService.deleteRepository( DeleteRepositoryParams.from( newRepoId ) );
        assertNull( this.repositoryService.get( newRepoId ) );

        this.snapshotService.restore( RestoreParams.create().
            repositoryId( newRepoId ).
            snapshotName( "my-snapshot" ).
            build() );

        // The system repo does not now about this repo now
        assertNull( this.repositoryService.get( newRepoId ) );

        this.snapshotService.restore( RestoreParams.create().
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            snapshotName( "my-snapshot" ).
            build() );

        assertNotNull( this.repositoryService.get( newRepoId ) );
        assertNotNull( this.repositoryService.get( SystemConstants.SYSTEM_REPO.getId() ) );
        assertNotNull( this.repositoryService.get( ContentConstants.CONTENT_REPO.getId() ) );
    }

    @Test
    public void restore_invalid_snapshot()
        throws Exception
    {
        assertThrows( SnapshotException.class, () -> NodeHelper.runAsAdmin( this::doRestoreInvalidSnapshot ) );
    }

    private void doRestoreInvalidSnapshot()
    {
        final RepositoryId newRepoId = RepositoryId.from( "new-repo" );
        this.repositoryService.createRepository( CreateRepositoryParams.create().
            repositoryId( newRepoId ).
            build() );

        this.snapshotService.snapshot( SnapshotParams.create().
            snapshotName( "my-snapshot" ).
            build() );

        this.snapshotService.delete( DeleteSnapshotParams.create().
            add( "my-snapshot" ).
            build() );

        this.repositoryService.deleteRepository( DeleteRepositoryParams.from( newRepoId ) );

        try
        {
            this.snapshotService.restore( RestoreParams.create().
                snapshotName( "my-snapshot" ).
                build() );
        }
        finally
        {
            assertNotNull( this.repositoryService.get( ContentConstants.CONTENT_REPO.getId() ) );
            assertNotNull( this.repositoryService.get( ContentConstants.CONTENT_REPO.getId() ) );
        }
    }

    @Test
    public void list_snapshots()
        throws Exception
    {
        NodeHelper.runAsAdmin( this::doListSnapshots );
    }

    private void doListSnapshots()
    {
        this.snapshotService.snapshot( SnapshotParams.create().
            snapshotName( "my-snapshot" ).
            build() );

        this.snapshotService.snapshot( SnapshotParams.create().
            snapshotName( "my-snapshot2" ).
            build() );

        final SnapshotResults result = this.snapshotService.list();

        assertEquals( 2, result.getSize() );
    }

    private static void cleanRepositories()
        throws IOException
    {
        for ( String repository : client.snapshotGetRepository( new GetRepositoriesRequest() ).repositories().stream().map(
            RepositoryMetaData::name ).collect( Collectors.toList() ) )
        {
            for ( String snapshot : client.snapshotGet( new GetSnapshotsRequest().
                repository( repository ) ).
                getSnapshots().stream().map( snapshotInfo -> snapshotInfo.snapshotId().getName() ).collect( Collectors.toList() ) )
            {
                client.snapshotDelete( new DeleteSnapshotRequest().
                    snapshot( snapshot ).
                    repository( repository ) );
            }
            client.snapshotDeleteRepository( new DeleteRepositoryRequest( repository ) );
        }
    }

}
