package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.List;
import java.util.Set;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotAction;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;

import com.google.common.collect.Lists;

import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;

public class SnapshotRestoreExecutor
    extends AbstractSnapshotExecutor
{
    private final String snapshotName;

    private final RepositoryId repositoryToRestore;

    private SnapshotRestoreExecutor( final Builder builder )
    {
        super( builder );
        repositoryToRestore = builder.repositoryToRestore;
        this.snapshotName = builder.snapshotName;
    }

    public RestoreResult execute()
    {
        final RestoreResult.Builder builder = RestoreResult.create();

        if ( this.repositoryToRestore == null )
        {
            addResult( builder, doRestoreRepo( SystemConstants.SYSTEM_REPO.getId() ) );
            getRepositories( false ).forEach( ( repo ) -> addResult( builder, doRestoreRepo( repo ) ) );
            return builder.build();
        }
        else
        {
            return doRestoreRepo( this.repositoryToRestore );
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

    private RestoreResult doRestoreRepo( final RepositoryId repositoryId )
    {
        final Set<String> indexNames = getIndexNames( repositoryId );
        return doRestoreIndices( indexNames );
    }

    private RestoreResult doRestoreIndices( final Set<String> indices )
    {
        try
        {
            closeIndices( indices );
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
        finally
        {
            openIndices( indices );
        }
    }

    private RestoreSnapshotResponse executeRestoreRequest( final Set<String> indices )
    {
        final RestoreSnapshotResponse response;
        final RestoreSnapshotRequestBuilder restoreSnapshotRequestBuilder =
            new RestoreSnapshotRequestBuilder( this.client.admin().cluster(), RestoreSnapshotAction.INSTANCE ).
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
        private String snapshotName;

        private RepositoryId repositoryToRestore;

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
