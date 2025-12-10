package com.enonic.xp.repo.impl.commit.storage;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageData;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repository.RepositoryId;

public class CommitStorageRequestFactory
{
    public static StoreRequest create( final NodeCommitEntry nodeCommitEntry, final RepositoryId repositoryId )
    {
        final String id = nodeCommitEntry.getNodeCommitId().toString();

        final StorageData data = StorageData.create()
            .add( CommitIndexPath.COMMIT_ID, id )
            .add( CommitIndexPath.MESSAGE, nodeCommitEntry.getMessage() )
            .add( CommitIndexPath.TIMESTAMP, nodeCommitEntry.getTimestamp() )
            .add( CommitIndexPath.COMMITTER, nodeCommitEntry.getCommitter().toString() )
            .build();

        return StoreRequest.create()
            .id( id )
            .storage( StorageSource.create()
                          .storageName( StoreStorageName.from( repositoryId ) )
                          .storageType( StaticStorageType.COMMIT )
                          .build() )
            .data( data )
            .build();
    }
}
