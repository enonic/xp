package com.enonic.wem.repo.internal.elasticsearch;

import java.time.Instant;

import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.snapshots.SnapshotInfo;

import com.enonic.xp.node.SnapshotResult;

class SnapshotResultFactory
{
    static SnapshotResult create( final CreateSnapshotResponse response )
    {
        final SnapshotInfo snapshotInfo = response.getSnapshotInfo();

        return doCreate( snapshotInfo );
    }

    static SnapshotResult create( final SnapshotInfo snapshotInfo )
    {
        return doCreate( snapshotInfo );
    }

    private static SnapshotResult doCreate( final SnapshotInfo snapshotInfo )
    {
        return SnapshotResult.create().
            state( SnapshotResult.State.valueOf( snapshotInfo.state().toString() ) ).
            indices( snapshotInfo.indices() ).
            name( snapshotInfo.name() ).
            reason( snapshotInfo.reason() ).
            timestamp( Instant.ofEpochMilli( snapshotInfo.endTime() ) ).
            build();
    }
}
