package com.enonic.wem.repo.internal.storage.branch;

import com.enonic.wem.repo.internal.branch.BranchContext;
import com.enonic.wem.repo.internal.branch.BranchDocumentId;
import com.enonic.wem.repo.internal.storage.DeleteRequest;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.xp.node.NodeId;

public class BranchDeleteRequestFactory
{
    public static DeleteRequest create( final NodeId nodeId, final BranchContext context )
    {
        return DeleteRequest.create().
            forceRefresh( true ).
            id( new BranchDocumentId( nodeId, context.getBranch() ).toString() ).
            settings( StorageSettings.create().
                storageName( StoreStorageName.from( context.getRepositoryId() ) ).
                storageType( StaticStorageType.BRANCH ).build() ).build();
    }

}
