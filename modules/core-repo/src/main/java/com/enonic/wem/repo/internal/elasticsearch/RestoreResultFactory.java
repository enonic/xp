package com.enonic.wem.repo.internal.elasticsearch;

import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.elasticsearch.snapshots.RestoreInfo;

import com.enonic.wem.api.snapshot.RestoreResult;

public class RestoreResultFactory
{
    static RestoreResult create( final RestoreSnapshotResponse response )
    {
        final RestoreInfo restoreInfo = response.getRestoreInfo();

        return RestoreResult.create().
            name( restoreInfo.name() ).
            indices( restoreInfo.indices() ).
            build();
    }

}
