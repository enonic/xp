package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.List;
import java.util.Set;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.ClusterManager;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.util.Exceptions;

public class SnapshotRestoreExecutor
    extends AbstractSnapshotExecutor
{
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

        final List<String> indices = Lists.newArrayList();
        indices.addAll( tempResults.getIndices() );
        indices.addAll( result.getIndices() );

        builder.indices( indices );
        builder.message( tempResults.getMessage() != null ? tempResults.getMessage() + " | " + result.getMessage() : result.getMessage() );
        builder.name( result.getName() );
    }

    private RestoreResult restoreAllRepositories()
    {
        final RestoreResult.Builder builder = RestoreResult.create();
        final RepositoryIds existingRepositoryIds = getRepositories( true );
        RepositoryIds currentRepositoryIds = null;

        try
        {
            //Closes  current indices and wait for ES provider to be disabled
            closeIndices( existingRepositoryIds );
            while ( !clusterManager.isHealthy() )
            {
                Thread.sleep( 1000l );
            }

            //Restore system indices
            addResult( builder, doRestoreRepo( SystemConstants.SYSTEM_REPO.getId() ) );
            openIndices( RepositoryIds.from( SystemConstants.SYSTEM_REPO.getId() ) );

            //Restore other indices
            currentRepositoryIds = getRepositories( false );
            closeIndices( currentRepositoryIds );
            currentRepositoryIds.forEach( ( repo ) -> addResult( builder, doRestoreRepo( repo ) ) );
            return builder.build();
        }
        catch ( InterruptedException e )
        {
            throw Exceptions.unchecked( e );
        }
        finally
        {
            //TODO Bug: Obsolete indices should be deleted
            openIndices( existingRepositoryIds );
            openIndices( currentRepositoryIds );
        }
    }

    private RestoreResult restoreSingleRepository( final RepositoryId repositoryId )
    {
        final RepositoryIds repositoryIds = RepositoryIds.from( repositoryId );
        try
        {
            closeIndices( repositoryIds );
            return doRestoreRepo( this.repositoryToRestore );
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
        final RestoreSnapshotResponse response;
        final RestoreSnapshotRequestBuilder restoreSnapshotRequestBuilder =
            new RestoreSnapshotRequestBuilder( this.client.admin().cluster() ).
                setRestoreGlobalState( false ).
                setIndices( indices.toArray( new String[indices.size()] ) ).
                setRepository( this.snapshotRepositoryName ).
                setSnapshot( this.snapshotName ).
                setWaitForCompletion( true );

        response = this.client.admin().cluster().restoreSnapshot( restoreSnapshotRequestBuilder.request() ).actionGet();
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
