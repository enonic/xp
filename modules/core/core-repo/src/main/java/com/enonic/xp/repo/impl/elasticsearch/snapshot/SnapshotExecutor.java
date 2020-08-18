package com.enonic.xp.repo.impl.elasticsearch.snapshot;


import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.common.settings.Settings;

import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;

public class SnapshotExecutor
    extends AbstractSnapshotExecutor
{
    private SnapshotExecutor( final Builder builder )
    {
        super( builder );
    }

    public SnapshotResult execute()
    {

        final CreateSnapshotRequest request = new CreateSnapshotRequest().
            indices( IndexNameResolver.resolveIndexNames( repositories ).toArray( String[]::new ) ).
            includeGlobalState( false ).
            waitForCompletion( true ).
            repository( this.snapshotRepositoryName ).
            snapshot( snapshotName ).
            settings( Settings.builder().
                put( "ignore_unavailable", true ) );

        final CreateSnapshotResponse response = client.snapshotCreate( request );

        return SnapshotResultFactory.create( response );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
        extends AbstractSnapshotExecutor.Builder<Builder>
    {
        private Builder()
        {
        }

        public SnapshotExecutor build()
        {
            return new SnapshotExecutor( this );
        }
    }
}
