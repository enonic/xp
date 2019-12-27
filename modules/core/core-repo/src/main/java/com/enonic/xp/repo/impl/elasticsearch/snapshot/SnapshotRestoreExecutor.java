package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterManager;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.util.Exceptions;

public class SnapshotRestoreExecutor
    extends AbstractSnapshotExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( SnapshotRestoreExecutor.class );

    private final ClusterManager clusterManager;

    private final String snapshotName;

    private final RepositoryId repositoryToRestore;

    private SnapshotRestoreExecutor( final Builder builder )
    {
        super( builder );
        clusterManager = builder.clusterManager;
        repositoryToRestore = builder.repositoryToRestore;
        this.snapshotName = builder.snapshotName;
    }

    public RestoreResult execute()
    {
        if ( this.repositoryToRestore == null )
        {
            return restoreAllRepositories();
        }
        else
        {
            return restoreSingleRepository( this.repositoryToRestore );
        }
    }

    private void addResult( final RestoreResult.Builder builder, RestoreResult result )
    {
        final RestoreResult tempResults = builder.build();

        if ( result.isFailed() )
        {
            builder.failed( true );
        }

        final List<String> indices = new ArrayList<>();
        indices.addAll( tempResults.getIndices() );
        indices.addAll( result.getIndices() );

        builder.indices( indices );
        builder.message( tempResults.getMessage() != null ? tempResults.getMessage() + " | " + result.getMessage() : result.getMessage() );
        builder.name( result.getName() );
    }

    private RestoreResult restoreAllRepositories()
    {
        final RepositoryIds repositoryIds = getRepositories( true );

        try
        {
            //Closes current indices and wait for ES provider to be disabled
            closeIndices( repositoryIds );
            while ( clusterManager.isHealthy() )
            {
                LOG.info( "Waiting for cluster providers to be deactivated to initiate restore" );
                Thread.sleep( 1000l );
            }

            //Restore indices
            return doRestoreIndices( Collections.emptySet() );
        }
        catch ( InterruptedException e )
        {
            throw Exceptions.unchecked( e );
        }
        finally
        {
            openIndices( repositoryIds );
        }
    }

    private RestoreResult restoreSingleRepository( final RepositoryId repositoryId )
    {
        final RepositoryIds repositoryIds = RepositoryIds.from( repositoryId );
        try
        {
            //Closes indices and wait for ES provider to be disabled
            closeIndices( repositoryIds );
            while ( clusterManager.isHealthy() )
            {
                Thread.sleep( 1000l );
            }

            //Restore indice
            return doRestoreRepo( this.repositoryToRestore );
        }
        catch ( InterruptedException e )
        {
            throw Exceptions.unchecked( e );
        }
        finally
        {
            openIndices( repositoryIds );
        }
    }

    private RestoreResult doRestoreRepo( final RepositoryId repositoryId )
    {
        final Set<String> indexNames = getIndexNames( repositoryId );
        return doRestoreIndices( indexNames );
    }

    private RestoreResult doRestoreIndices( final Set<String> indices )
    {
        try
        {
            final RestoreSnapshotResponse response = executeRestoreRequest( indices );
            return RestoreResultFactory.create( response, repositoryToRestore );
        }
        catch ( ElasticsearchException e )
        {
            return RestoreResult.create().
                repositoryId( repositoryToRestore ).
                indices( indices ).
                failed( true ).
                name( snapshotName ).
                message( "Could not restore snapshot: " + e.toString() + " for indices: " + indices ).
                build();
        }
    }

    private RestoreSnapshotResponse executeRestoreRequest( final Set<String> indices )
    {
        final RestoreSnapshotRequest request = new RestoreSnapshotRequest().
            includeGlobalState( false ).
            indices( indices.toArray( new String[indices.size()] ) ).
            repository( snapshotRepositoryName ).
            snapshot( snapshotName ).
            waitForCompletion( true );

        final RestoreSnapshotResponse response = client.snapshotRestore( request );
        return response;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractSnapshotExecutor.Builder<Builder>
    {
        private ClusterManager clusterManager;

        private String snapshotName;

        private RepositoryId repositoryToRestore;

        public Builder clusterManager( final ClusterManager clusterManager )
        {
            this.clusterManager = clusterManager;
            return this;
        }

        public Builder repositoryToRestore( final RepositoryId repositoryToRestore )
        {
            this.repositoryToRestore = repositoryToRestore;
            return this;
        }

        public Builder snapshotName( final String snapshotName )
        {
            this.snapshotName = snapshotName;
            return this;
        }

        public SnapshotRestoreExecutor build()
        {
            return new SnapshotRestoreExecutor( this );
        }

    }

}
