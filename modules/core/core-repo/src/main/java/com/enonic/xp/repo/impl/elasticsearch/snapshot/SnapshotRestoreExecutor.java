package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.Arrays;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotAction;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;

import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repository.RepositoryIds;

public class SnapshotRestoreExecutor
    extends AbstractSnapshotExecutor
{
    private final IndexServiceInternal indexServiceInternal;

    private final RepositoryIds repositoriesToClose;

    private final RepositoryIds repositoriesToRestore;

    private SnapshotRestoreExecutor( final Builder builder )
    {
        super( builder );
        this.indexServiceInternal = builder.indexServiceInternal;
        this.repositoriesToClose = builder.repositoriesToClose;
        this.repositoriesToRestore = builder.repositoriesToRestore;
    }

    public RestoreResult execute()
    {
        final String[] indicesToClose = IndexNameResolver.resolveIndexNames( repositoriesToClose ).toArray( String[]::new );
        final String[] indicesToRestore = IndexNameResolver.resolveIndexNames( repositoriesToRestore ).toArray( String[]::new );

        indexServiceInternal.closeIndices( indicesToClose );
        try
        {
            final RestoreSnapshotResponse response = executeRestoreRequest( indicesToRestore );
            return RestoreResultFactory.create( response, null );
        }
        catch ( ElasticsearchException e )
        {
            return RestoreResult.create()
                .repositoryId( null )
                .indices( List.of( indicesToRestore ) )
                .failed( true )
                .name( snapshotName )
                .message( "Could not restore snapshot: " + e + " for indices: " + Arrays.asList( indicesToClose ) )
                .build();
        }
        finally
        {
            indexServiceInternal.openIndices( indicesToClose );
        }
    }

    private RestoreSnapshotResponse executeRestoreRequest( final String... indices )
    {
        final RestoreSnapshotResponse response;
        final RestoreSnapshotRequestBuilder restoreSnapshotRequestBuilder =
            new RestoreSnapshotRequestBuilder( this.client.admin().cluster(), RestoreSnapshotAction.INSTANCE ).
                setRestoreGlobalState( false ).
                setIndices( indices ).
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
        private IndexServiceInternal indexServiceInternal;

        private RepositoryIds repositoriesToClose;

        private RepositoryIds repositoriesToRestore;

        public Builder indexServiceInternal( final IndexServiceInternal indexServiceInternal )
        {
            this.indexServiceInternal = indexServiceInternal;
            return this;
        }

        public Builder repositoriesToClose( final RepositoryIds repositories )
        {
            this.repositoriesToClose = repositories;
            return this;
        }

        public Builder repositoriesToRestore( final RepositoryIds repositories )
        {
            this.repositoriesToRestore = repositories;
            return this;
        }

        public SnapshotRestoreExecutor build()
        {
            return new SnapshotRestoreExecutor( this );
        }
    }
}
