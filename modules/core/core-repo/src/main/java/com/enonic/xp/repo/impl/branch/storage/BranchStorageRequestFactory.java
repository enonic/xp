package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageData;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repository.RepositoryId;

class BranchStorageRequestFactory
{
    public static StoreRequest create( final NodeBranchEntry nodeBranchEntry, final RepositoryId repositoryId, final Branch branch )
    {
        final StorageData data = StorageData.create().
            add( BranchIndexPath.VERSION_ID.getPath(), nodeBranchEntry.getVersionId().toString() ).
            add( BranchIndexPath.NODE_BLOB_KEY.getPath(), nodeBranchEntry.getNodeVersionKey().getNodeBlobKey().toString() ).
            add( BranchIndexPath.INDEX_CONFIG_BLOB_KEY.getPath(), nodeBranchEntry.getNodeVersionKey().getIndexConfigBlobKey().toString() ).
            add( BranchIndexPath.ACCESS_CONTROL_BLOB_KEY.getPath(),
                 nodeBranchEntry.getNodeVersionKey().getAccessControlBlobKey().toString() ).
            add( BranchIndexPath.BRANCH_NAME.getPath(), branch.getValue() ).
            add( BranchIndexPath.NODE_ID.getPath(), nodeBranchEntry.getNodeId().toString() ).
            add( BranchIndexPath.PATH.getPath(), nodeBranchEntry.getNodePath().toString() ).
            add( BranchIndexPath.TIMESTAMP.getPath(), nodeBranchEntry.getTimestamp() ).
            build();

        final NodeId nodeId = nodeBranchEntry.getNodeId();

        return StoreRequest.create().id( BranchDocumentId.asString( nodeId, branch ) ).nodePath( nodeBranchEntry.getNodePath() ).
            settings( StorageSource.create().
                storageName( StoreStorageName.from( repositoryId ) ).
                storageType( StaticStorageType.BRANCH ).
                build() ).
            data( data ).
            parent( nodeBranchEntry.getVersionId().toString() ).
            routing( nodeId.toString() ).
            build();
    }
}
