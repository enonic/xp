package com.enonic.xp.repo.impl.commit.storage;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.storage.CommitStorageName;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageData;
import com.enonic.xp.repo.impl.storage.StoreRequest;

public class CommitStorageRequestFactory
{
    public static StoreRequest create( final NodeCommitEntry nodeCommitEntry, final InternalContext context )
    {
        final String id = nodeCommitEntry.getNodeCommitId().toString();

        final StorageData data = StorageData.create().
            add( CommitIndexPath.COMMIT_ID.getPath(), id ).
            add( CommitIndexPath.MESSAGE.getPath(), nodeCommitEntry.getMessage() ).
            add( CommitIndexPath.TIMESTAMP.getPath(), nodeCommitEntry.getTimestamp() ).
            add( CommitIndexPath.COMMITTER.getPath(), nodeCommitEntry.getCommitter().toString() ).
            build();

        return StoreRequest.create().
            id( id ).
            forceRefresh( false ).
            settings( StorageSource.create().
                storageName( CommitStorageName.from( context.getRepositoryId() ) ).
                storageType( StaticStorageType.COMMIT ).
                build() ).
            data( data ).
            build();
    }
}
