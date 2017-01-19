package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.io.File;
import java.time.Instant;
import java.util.Set;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.repositories.RepositoryException;
import org.elasticsearch.repositories.RepositoryMissingException;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.elasticsearch.snapshots.SnapshotState;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.snapshot.SnapshotService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositoryService;

@Component
public class SnapshotServiceImpl
    implements SnapshotService
{
    private final static String SNAPSHOT_REPOSITORY_NAME = "enonic-xp-snapshot-repo";

    private final static Logger LOG = LoggerFactory.getLogger( SnapshotServiceImpl.class );

    private Client client;

    private RepoConfiguration configuration;

    private RepositoryService repositoryService;

    @Override
    public SnapshotResult snapshot( final SnapshotParams snapshotParams )
    {
        return NodeHelper.runAsAdmin( () -> doSnapshot( snapshotParams ) );
    }

    private SnapshotResult doSnapshot( final SnapshotParams snapshotParams )
    {
        checkSnapshotRepository();

        final Set<String> indices = getSnapshotIndexNames( snapshotParams.getRepositoryId(), snapshotParams.isIncludeIndexedData() );

        final CreateSnapshotRequestBuilder createRequest = new CreateSnapshotRequestBuilder( this.client.admin().cluster() ).
            setIndices( indices.toArray( new String[indices.size()] ) ).
            setIncludeGlobalState( false ).
            setWaitForCompletion( true ).
            setRepository( SNAPSHOT_REPOSITORY_NAME ).
            setSnapshot( snapshotParams.getSnapshotName() ).
            setSettings( ImmutableSettings.settingsBuilder().
                put( "ignore_unavailable", true ) );

        final CreateSnapshotResponse createSnapshotResponse =
            this.client.admin().cluster().createSnapshot( createRequest.request() ).actionGet();

        return SnapshotResultFactory.create( createSnapshotResponse );
    }

    @Override
    public RestoreResult restore( final RestoreParams restoreParams )
    {
        return NodeHelper.runAsAdmin( () -> doRestore( restoreParams ) );
    }

    private RestoreResult doRestore( final RestoreParams restoreParams )
    {
        checkSnapshotRepository();

        final RepositoryId repositoryId = restoreParams.getRepositoryId();
        final String snapshotName = restoreParams.getSnapshotName();

        validateSnapshot( snapshotName );

        final Set<String> indices = getSnapshotIndexNames( repositoryId, restoreParams.isIncludeIndexedData() );

        closeIndices( indices );

        final RestoreSnapshotResponse response;
        try
        {
            final RestoreSnapshotRequestBuilder restoreSnapshotRequestBuilder =
                new RestoreSnapshotRequestBuilder( this.client.admin().cluster() ).
                    setRestoreGlobalState( false ).
                    setIndices( indices.toArray( new String[indices.size()] ) ).
                    setRepository( SNAPSHOT_REPOSITORY_NAME ).
                    setSnapshot( snapshotName ).
                    setWaitForCompletion( true );

            response = this.client.admin().cluster().restoreSnapshot( restoreSnapshotRequestBuilder.request() ).actionGet();

            return RestoreResultFactory.create( response, repositoryId );
        }
        catch ( ElasticsearchException e )
        {
            return RestoreResult.create().
                repositoryId( repositoryId ).
                indices( indices ).
                failed( true ).
                name( snapshotName ).
                message( "Could not restore snapshot: " + e.toString() + " to repository " + repositoryId ).
                build();
        }
        finally
        {
            openIndices( indices );
        }
    }

    @Override
    public SnapshotResults list()
    {
        return doListSnapshots();
    }

    private SnapshotResults doListSnapshots()
    {
        checkSnapshotRepository();

        final GetSnapshotsRequest getSnapshotsRequest = new GetSnapshotsRequest( SNAPSHOT_REPOSITORY_NAME );

        final GetSnapshotsResponse getSnapshotsResponse = this.client.admin().cluster().getSnapshots( getSnapshotsRequest ).actionGet();

        return SnapshotResultsFactory.create( getSnapshotsResponse );
    }

    @Override
    public DeleteSnapshotsResult delete( final DeleteSnapshotParams params )
    {
        return NodeHelper.runAsAdmin( () -> doDelete( params ) );
    }


    private void validateSnapshot( final String snapshotName )
    {
        try
        {
            final SnapshotInfo snapshot = this.getSnapshot( snapshotName );

            if ( snapshot == null )
            {
                throw new SnapshotException( "Failed to restore snapshot: Snapshot with name '" + snapshotName +
                                                 "' not found" );
            }

            if ( snapshot.state().equals( SnapshotState.FAILED ) )
            {
                throw new SnapshotException( "Failed to restore snapshot: Snapshot with name '" + snapshotName +
                                                 "' is not valid" );
            }
        }
        catch ( RepositoryMissingException e )
        {
            throw new SnapshotException( "Failed to restore snapshot [" + snapshotName +
                                             "]: Repository not initialized '" );
        }
        catch ( Exception e )
        {
            throw new SnapshotException( "Failed to restore snapshot", e );
        }
    }


    private void checkSnapshotRepository()
    {
        if ( !snapshotRepositoryExists() )
        {
            registerRepository();
        }
    }

    private SnapshotInfo getSnapshot( final String snapshotName )
    {
        final GetSnapshotsRequestBuilder getSnapshotsRequestBuilder = new GetSnapshotsRequestBuilder( this.client.admin().cluster() ).
            setRepository( SNAPSHOT_REPOSITORY_NAME ).
            setSnapshots( snapshotName );

        final GetSnapshotsResponse getSnapshotsResponse =
            this.client.admin().cluster().getSnapshots( getSnapshotsRequestBuilder.request() ).actionGet();

        final ImmutableList<SnapshotInfo> snapshots = getSnapshotsResponse.getSnapshots();

        if ( snapshots.size() == 0 )
        {
            return null;
        }
        else
        {
            return snapshots.get( 0 );
        }
    }

    private Set<String> getSnapshotIndexNames( final RepositoryId repositoryId, final boolean includeIndexedData )
    {
        final Set<String> indices = Sets.newHashSet();

        final RepositoryIds repositoryIds =
            repositoryId == null ? this.repositoryService.list().getIds() : RepositoryIds.from( repositoryId );

        for ( RepositoryId currentRepositoryId : repositoryIds )
        {
            indices.add( IndexNameResolver.resolveStorageIndexName( currentRepositoryId ) );

            if ( includeIndexedData )
            {
                indices.add( IndexNameResolver.resolveSearchIndexName( currentRepositoryId ) );
            }
        }

        return indices;
    }

    private DeleteSnapshotsResult doDelete( final DeleteSnapshotParams params )
    {
        final DeleteSnapshotsResult.Builder builder = DeleteSnapshotsResult.create();

        if ( !params.getSnapshotNames().isEmpty() )
        {
            builder.addAll( deleteByName( params.getSnapshotNames() ) );
        }

        if ( params.getBefore() != null )
        {
            builder.addAll( deleteByBefore( params.getBefore() ) );
        }

        return builder.build();
    }

    private Set<String> deleteByBefore( final Instant before )
    {
        final Set<String> deleted = Sets.newHashSet();

        final SnapshotResults snapshotResults = doListSnapshots();

        for ( final SnapshotResult snapshotResult : snapshotResults )
        {
            if ( snapshotResult.getTimestamp().isBefore( before ) )
            {
                doDeleteSnapshot( snapshotResult.getName() );
                deleted.add( snapshotResult.getName() );
            }
        }

        return deleted;
    }

    private Set<String> deleteByName( final Set<String> snapshotNames )
    {
        final Set<String> deletedNames = Sets.newHashSet();

        for ( final String name : snapshotNames )
        {
            doDeleteSnapshot( name );
            deletedNames.add( name );
        }

        return deletedNames;
    }

    private void doDeleteSnapshot( final String snapshotName )
    {
        checkSnapshotRepository();

        final DeleteSnapshotRequest deleteSnapshotRequest = new DeleteSnapshotRequest( SNAPSHOT_REPOSITORY_NAME, snapshotName );

        this.client.admin().cluster().deleteSnapshot( deleteSnapshotRequest ).actionGet();
    }

    private void openIndices( final Set<String> indexNames )
    {
        for ( final String indexName : indexNames )
        {
            OpenIndexRequestBuilder openIndexRequestBuilder = new OpenIndexRequestBuilder( this.client.admin().indices() ).
                setIndices( indexName );

            try
            {
                this.client.admin().indices().open( openIndexRequestBuilder.request() ).actionGet();
                LOG.info( "Opened index " + indexName );
            }
            catch ( ElasticsearchException e )
            {
                LOG.warn( "Could not open index [" + indexName + "]" );
            }
        }
    }

    private void closeIndices( final Set<String> indexNames )
    {
        for ( final String indexName : indexNames )
        {
            CloseIndexRequestBuilder closeIndexRequestBuilder = new CloseIndexRequestBuilder( this.client.admin().indices() ).
                setIndices( indexName );

            try
            {
                this.client.admin().indices().close( closeIndexRequestBuilder.request() ).actionGet();
                LOG.info( "Closed index " + indexName );
            }
            catch ( IndexMissingException e )
            {
                LOG.warn( "Could not close index [" + indexName + "], not found" );
            }
        }
    }

    private boolean snapshotRepositoryExists()
    {
        final GetRepositoriesRequest getRepositoriesRequest = new GetRepositoriesRequest( new String[]{SNAPSHOT_REPOSITORY_NAME} );

        try
        {
            final GetRepositoriesResponse response = this.client.admin().cluster().getRepositories( getRepositoriesRequest ).actionGet();
            return !response.repositories().isEmpty();
        }
        catch ( RepositoryException e )
        {
            return false;
        }
    }

    private File getSnapshotsDir()
    {
        return this.configuration.getSnapshotsDir();
    }

    private void registerRepository()
    {
        final PutRepositoryRequestBuilder requestBuilder = new PutRepositoryRequestBuilder( this.client.admin().cluster() ).
            setName( SNAPSHOT_REPOSITORY_NAME ).
            setType( "fs" ).
            setSettings( ImmutableSettings.settingsBuilder().
                put( "compress", true ).
                put( "location", getSnapshotsDir() ).
                build() );

        this.client.admin().cluster().putRepository( requestBuilder.request() ).actionGet();
    }


    @Reference
    public void setConfiguration( final RepoConfiguration configuration )
    {
        this.configuration = configuration;
    }

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }
}
