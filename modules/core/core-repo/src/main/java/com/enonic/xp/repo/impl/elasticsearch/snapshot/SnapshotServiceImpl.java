package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.io.File;
import java.time.Instant;
import java.util.Set;

import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.repositories.RepositoryException;
import org.elasticsearch.repositories.RepositoryMissingException;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.elasticsearch.snapshots.SnapshotState;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Sets;

import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.snapshot.SnapshotService;

@Component
public class SnapshotServiceImpl
    implements SnapshotService
{
    private final static String SNAPSHOT_REPOSITORY_NAME = "enonic-xp-snapshot-repo";

    private Client client;

    private RepoConfiguration configuration;

    private RepositoryService repositoryService;

    private EventPublisher eventPublisher;

    @Override
    public SnapshotResult snapshot( final SnapshotParams snapshotParams )
    {
        return NodeHelper.runAsAdmin( () -> doSnapshot( snapshotParams ) );
    }

    private SnapshotResult doSnapshot( final SnapshotParams snapshotParams )
    {
        checkSnapshotRepository();

        return SnapshotExecutor.create().
            snapshotName( snapshotParams.getSnapshotName() ).
            repositoryToSnapshot( snapshotParams.getRepositoryId() ).
            client( this.client ).
            repositoryService( this.repositoryService ).
            snapshotRepositoryName( SNAPSHOT_REPOSITORY_NAME ).
            build().
            execute();
    }

    @Override
    public RestoreResult restore( final RestoreParams restoreParams )
    {
        return NodeHelper.runAsAdmin( () -> doRestore( restoreParams ) );
    }

    private RestoreResult doRestore( final RestoreParams restoreParams )
    {
        checkSnapshotRepository();
        validateSnapshot( restoreParams.getSnapshotName() );

        this.eventPublisher.publish( RepositoryEvents.restoreInitialized() );

        final RestoreResult result = SnapshotRestoreExecutor.create().
            repositoryToRestore( restoreParams.getRepositoryId() ).
            snapshotName( restoreParams.getSnapshotName() ).
            client( this.client ).
            repositoryService( this.repositoryService ).
            snapshotRepositoryName( SNAPSHOT_REPOSITORY_NAME ).
            build().
            execute();

        this.eventPublisher.publish( RepositoryEvents.restored() );

        return result;
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
                throw new SnapshotException( "Failed to restore snapshot: Snapshot with name '" + snapshotName + "' not found" );
            }

            if ( snapshot.state().equals( SnapshotState.FAILED ) )
            {
                throw new SnapshotException( "Failed to restore snapshot: Snapshot with name '" + snapshotName + "' is not valid" );
            }
        }
        catch ( RepositoryMissingException e )
        {
            throw new SnapshotException( "Failed to restore snapshot [" + snapshotName + "]: Repository not initialized '" );
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

    private boolean snapshotRepositoryExists()
    {
        final RepositoryMetaData snapshotRepo = getSnapshotRepo();

        if ( snapshotRepo == null )
        {
            return false;
        }

        final boolean sameAsConfiguredLocation = snapshotRepo.settings().get( "location" ).equals( getSnapshotsDir().getPath() );

        if ( !sameAsConfiguredLocation )
        {
            return false;
        }

        return true;
    }

    private RepositoryMetaData getSnapshotRepo()
    {
        try
        {
            final GetRepositoriesRequest getRepositoriesRequest = new GetRepositoriesRequest( new String[]{SNAPSHOT_REPOSITORY_NAME} );

            final GetRepositoriesResponse response = this.client.admin().cluster().getRepositories( getRepositoriesRequest ).actionGet();

            for ( final RepositoryMetaData repo : response.repositories() )
            {
                if ( repo.name().equals( SNAPSHOT_REPOSITORY_NAME ) )
                {
                    return repo;
                }
            }
        }
        catch ( RepositoryException e )
        {
            return null;
        }

        return null;
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

    private File getSnapshotsDir()
    {
        return this.configuration.getSnapshotsDir();
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

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }
}
