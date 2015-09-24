package com.enonic.wem.repo.internal.elasticsearch;

import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.elasticsearch.snapshots.RestoreInfo;

import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.repository.RepositoryId;

class RestoreResultFactory
{
    static RestoreResult create( final RestoreSnapshotResponse response, final RepositoryId respositoryId )
    {
        final RestoreInfo restoreInfo = response.getRestoreInfo();

        return RestoreResult.create().
            repositoryId( respositoryId ).
            name( restoreInfo.name() ).
            indices( restoreInfo.indices() ).
            message( "Restore successfull, " + response.getRestoreInfo().successfulShards() + " shards restored" ).
            build();
    }

}
