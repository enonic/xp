package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.elasticsearch.snapshots.RestoreInfo;

import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.repository.RepositoryId;

class RestoreResultFactory
{
    static RestoreResult create( final RestoreSnapshotResponse response, final RepositoryId respositoryId )
    {
        final RestoreInfo restoreInfo = response.getRestoreInfo();

        if ( restoreInfo.failedShards() > 0 )
        {
            return RestoreResult.create().
                failed( true ).
                name( restoreInfo.name() ).
                indices( restoreInfo.indices() ).
                message( "Restore failed, " + response.getRestoreInfo().failedShards() + " of " + response.getRestoreInfo().totalShards() +
                             " shards failed" ).
                build();
        }

        return RestoreResult.create().
            repositoryId( respositoryId ).
            name( restoreInfo.name() ).
            indices( restoreInfo.indices() ).
            message( "Restore successfull, " + response.getRestoreInfo().successfulShards() + " shards restored" ).
            build();
    }

}
