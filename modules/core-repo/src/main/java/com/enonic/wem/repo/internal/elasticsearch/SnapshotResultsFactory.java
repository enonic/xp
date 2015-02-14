package com.enonic.wem.repo.internal.elasticsearch;

import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;

import com.enonic.xp.core.snapshot.SnapshotResults;

class SnapshotResultsFactory
{

    static SnapshotResults create( final GetSnapshotsResponse response )
    {
        final SnapshotResults.Builder builder = SnapshotResults.create();

        for ( final org.elasticsearch.snapshots.SnapshotInfo esSnapshotInfo : response.getSnapshots() )
        {
            builder.add( SnapshotResultFactory.create( esSnapshotInfo ) );
        }

        return builder.build();
    }
}
