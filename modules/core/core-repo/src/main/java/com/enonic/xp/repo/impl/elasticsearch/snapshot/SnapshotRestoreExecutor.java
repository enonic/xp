package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.Arrays;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotAction;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequest;

import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;

public class SnapshotRestoreExecutor
    extends AbstractSnapshotExecutor
{
    private final IndexServiceInternal indexServiceInternal;

    private SnapshotRestoreExecutor( final Builder builder )
    {
        super( builder );
        this.indexServiceInternal = builder.indexServiceInternal;
    }

    public RestoreResult execute()
    {
        final String[] indices = IndexNameResolver.resolveIndexNames( repositories ).toArray( String[]::new );

        indexServiceInternal.closeIndices( indices );
        try
        {
            final RestoreSnapshotResponse response = executeRestoreRequest( indices );
            return RestoreResultFactory.create( response, null );
        }
        catch ( ElasticsearchException e )
        {
            return RestoreResult.create().
                repositoryId( null ).
                indices( Arrays.asList( indices ) ).
                failed( true ).
                name( snapshotName ).
                message( "Could not restore snapshot: " + e.toString() + " for indices: " + Arrays.asList( indices ) ).
                build();
        }
        finally
        {
            indexServiceInternal.openIndices( indices );
        }
    }

    private RestoreSnapshotResponse executeRestoreRequest( final String... indices )
    {

        final RestoreSnapshotRequest request = new RestoreSnapshotRequest().
            includeGlobalState( false ).
            indices( indices.toArray( new String[0] ) ).
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
        private IndexServiceInternal indexServiceInternal;

        public Builder indexServiceInternal( final IndexServiceInternal indexServiceInternal )
        {
            this.indexServiceInternal = indexServiceInternal;
            return this;
        }

        public SnapshotRestoreExecutor build()
        {
            return new SnapshotRestoreExecutor( this );
        }
    }
}
