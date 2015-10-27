package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.storage.DeleteRequest;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StoreStorageName;

class BranchDeleteRequestFactory
{
    public static DeleteRequest create( final NodeId nodeId, final InternalContext context )
    {
        return DeleteRequest.create().
            forceRefresh( true ).
            id( new BranchDocumentId( nodeId, context.getBranch() ).toString() ).
            settings( StorageSettings.create().
                storageName( StoreStorageName.from( context.getRepositoryId() ) ).
                storageType( StaticStorageType.BRANCH ).build() ).build();
    }

}
