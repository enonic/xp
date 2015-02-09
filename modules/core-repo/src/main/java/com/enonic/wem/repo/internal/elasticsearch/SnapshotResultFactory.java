package com.enonic.wem.repo.internal.elasticsearch;

import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.snapshots.SnapshotInfo;

import com.enonic.wem.api.snapshot.SnapshotResult;

public class SnapshotResultFactory
{
    static SnapshotResult create( final CreateSnapshotResponse response )
    {
        final SnapshotInfo snapshotInfo = response.getSnapshotInfo();

        return SnapshotResult.create().
            state( SnapshotResult.State.valueOf( snapshotInfo.state().toString() ) ).
            indices( snapshotInfo.indices() ).
            name( snapshotInfo.name() ).
            reason( snapshotInfo.reason() ).
            build();
    }
}
