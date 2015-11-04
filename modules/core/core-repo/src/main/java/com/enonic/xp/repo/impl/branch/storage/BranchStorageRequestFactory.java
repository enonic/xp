package com.enonic.xp.repo.impl.branch.storage;

import java.time.Instant;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageData;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repo.impl.version.NodeVersionDocumentId;

class BranchStorageRequestFactory
{
    public static StoreRequest create( final NodeBranchMetadata nodeBranchMetadata, final InternalContext context )
    {

        final StorageData data = StorageData.create().
            add( BranchIndexPath.VERSION_ID.getPath(), nodeBranchMetadata.getVersionId().toString() ).
            add( BranchIndexPath.BRANCH_NAME.getPath(), context.getBranch().getName() ).
            add( BranchIndexPath.NODE_ID.getPath(), nodeBranchMetadata.getNodeId().toString() ).
            add( BranchIndexPath.STATE.getPath(), nodeBranchMetadata.getNodeState().value() ).
            add( BranchIndexPath.PATH.getPath(), nodeBranchMetadata.getNodePath().toString() ).
            add( BranchIndexPath.TIMESTAMP.getPath(),
                 nodeBranchMetadata.getTimestamp() != null ? nodeBranchMetadata.getTimestamp() : Instant.now() ).
            build();

        final NodeId nodeId = nodeBranchMetadata.getNodeId();

        return StoreRequest.create().
            id( new BranchDocumentId( nodeId, context.getBranch() ).toString() ).
            nodePath( nodeBranchMetadata.getNodePath() ).
            forceRefresh( false ).
            settings( StorageSettings.create().
                storageName( StoreStorageName.from( context.getRepositoryId() ) ).
                storageType( StaticStorageType.BRANCH ).
                build() ).
            data( data ).
            parent( new NodeVersionDocumentId( nodeId, nodeBranchMetadata.getVersionId() ).toString() ).
            routing( nodeId.toString() ).
            build();
    }


}
