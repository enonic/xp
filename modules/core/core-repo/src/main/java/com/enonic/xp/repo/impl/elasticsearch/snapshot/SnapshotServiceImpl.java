package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.nio.file.Path;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryAction;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsAction;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.repositories.RepositoryException;
import org.elasticsearch.repositories.RepositoryMissingException;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.elasticsearch.snapshots.SnapshotState;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.repository.RepositoryEntryService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.snapshot.SnapshotService;

@Component
public class SnapshotServiceImpl
    implements SnapshotService
{
    private static final Logger LOG = LoggerFactory.getLogger( SnapshotServiceImpl.class );

    private static final String SNAPSHOT_REPOSITORY_NAME = "enonic-xp-snapshot-repo";

    private final Client client;

    private final RepoConfiguration configuration;

    private final RepositoryEntryService repositoryEntryService;

    private final EventPublisher eventPublisher;

    private final IndexServiceInternal indexServiceInternal;

    @Activate
    public SnapshotServiceImpl( @Reference final Client client, @Reference final RepoConfiguration configuration, @Reference final RepositoryEntryService repositoryEntryService,
                                @Reference final EventPublisher eventPublisher, @Reference final IndexServiceInternal indexServiceInternal )
    {
        this.client = client;
        this.configuration = configuration;
        this.repositoryEntryService = repositoryEntryService;
        this.eventPublisher = eventPublisher;
        this.indexServiceInternal = indexServiceInternal;
    }

    @Override
    public SnapshotResult snapshot( final SnapshotParams snapshotParams )
    {
        return NodeHelper.runAsAdmin( () -> doSnapshot( snapshotParams ) );
    }

    private SnapshotResult doSnapshot( final SnapshotParams snapshotParams )
    {
        checkSnapshotRepository();

        final RepositoryIds repositoriesToSnapshot = Optional.ofNullable( snapshotParams.getRepositoryId() )
            .map( RepositoryIds::from )
            .orElseGet( repositoryEntryService::findRepositoryEntryIds );

        return SnapshotExecutor.create()
            .snapshotName( snapshotParams.getSnapshotName() )
            .repositories( repositoriesToSnapshot )
            .client( this.client )
            .snapshotRepositoryName( SNAPSHOT_REPOSITORY_NAME )
            .build()
            .execute();
    }

    @Override
    public RestoreResult restore( final RestoreParams restoreParams )
    {
        return NodeHelper.runAsAdmin( () -> doRestore( restoreParams ) );
    }

    private RestoreResult doRestore( final RestoreParams restoreParams )
    {
        checkSnapshotRepository();

        final String snapshotName = restoreParams.isLatest() ? determineNameOfLatestSnapshot() : restoreParams.getSnapshotName();

        validateSnapshot( snapshotName );

        final RepositoryId repositoryToRestore = restoreParams.getRepositoryId();
        boolean restoreAll = repositoryToRestore == null;

        final RepositoryIds repositoriesBeforeRestore = restoreAll ? repositoryEntryService.findRepositoryEntryIds() : null;

        this.eventPublisher.publish( RepositoryEvents.restoreInitialized() );

        if ( restoreAll )
        {
            LOG.info( "Restoring all repositories from snapshot" );
        }
        else
        {
            LOG.info( "Restoring repository {} from snapshot", repositoryToRestore );
        }

        final RestoreResult result = SnapshotRestoreExecutor.create()
            .snapshotName( snapshotName )
            .repositoriesToClose( restoreAll ? repositoriesBeforeRestore : RepositoryIds.from( repositoryToRestore ) )
            .repositoriesToRestore( restoreAll ? RepositoryIds.empty() : RepositoryIds.from( repositoryToRestore ) )
            .client( this.client )
            .snapshotRepositoryName( SNAPSHOT_REPOSITORY_NAME )
            .indexServiceInternal( this.indexServiceInternal )
            .build()
            .execute();

        if ( restoreAll )
        {
            final RepositoryIds repositoriesAfterRestore = repositoryEntryService.findRepositoryEntryIds();

            repositoriesBeforeRestore.stream().filter( Predicate.not( repositoriesAfterRestore::contains ) ).forEach( repositoryId -> {
                LOG.info( "Deleting repository {} indices missing in snapshot", repositoryId );
                indexServiceInternal.deleteIndices( IndexNameResolver.resolveIndexNames( repositoryId ).toArray( String[]::new ) );
            } );
        }

        LOG.info( "Snapshot Restore completed" );
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

        return snapshotRepo.settings().get( "location" ).equals( getSnapshotsDir().toString() );
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
        final PutRepositoryRequestBuilder requestBuilder =
            new PutRepositoryRequestBuilder( this.client.admin().cluster(), PutRepositoryAction.INSTANCE ).setName(
                    SNAPSHOT_REPOSITORY_NAME )
                .setType( "fs" )
                .setSettings( Settings.settingsBuilder().put( "compress", true ).put( "location", getSnapshotsDir() ).build() );

        this.client.admin().cluster().putRepository( requestBuilder.request() ).actionGet();
    }

    private Path getSnapshotsDir()
    {
        return this.configuration.getSnapshotsDir();
    }

    private SnapshotInfo getSnapshot( final String snapshotName )
    {
        final GetSnapshotsRequestBuilder getSnapshotsRequestBuilder =
            new GetSnapshotsRequestBuilder( this.client.admin().cluster(), GetSnapshotsAction.INSTANCE ).setRepository(
                SNAPSHOT_REPOSITORY_NAME ).setSnapshots( snapshotName );

        final GetSnapshotsResponse getSnapshotsResponse =
            this.client.admin().cluster().getSnapshots( getSnapshotsRequestBuilder.request() ).actionGet();

        final List<SnapshotInfo> snapshots = getSnapshotsResponse.getSnapshots();

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
        final Set<String> deleted = new HashSet<>();

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
        final Set<String> deletedNames = new HashSet<>();

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

    private String determineNameOfLatestSnapshot()
    {
        final SnapshotResults snapshotResults = list();

        if ( snapshotResults == null || snapshotResults.isEmpty() )
        {
            throw new SnapshotException( "No snapshots found" );
        }

        final SnapshotResult snapshotResult = snapshotResults.getSet()
            .stream()
            .skip( snapshotResults.getSize() - 1 )
            .findFirst()
            .orElseThrow( () -> new SnapshotException( "No snapshots found" ) );

        return snapshotResult.getName();
    }
}
