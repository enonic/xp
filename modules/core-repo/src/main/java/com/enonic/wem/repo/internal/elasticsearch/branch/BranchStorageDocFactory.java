package com.enonic.wem.repo.internal.elasticsearch.branch;

import java.time.Instant;

import com.enonic.wem.repo.internal.branch.BranchContext;
import com.enonic.wem.repo.internal.branch.BranchDocumentId;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageData;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreRequest;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.wem.repo.internal.version.NodeVersionDocumentId;

public class BranchStorageDocFactory
{
    public static StoreRequest create( final StoreBranchDocument doc, final BranchContext context )
    {
        final StorageData data = StorageData.create().
            addStringValue( BranchIndexPath.VERSION_ID.getPath(), doc.getNodeVersionId().toString() ).
            addStringValue( BranchIndexPath.BRANCH_NAME.getPath(), context.getBranch().getName() ).
            addStringValue( BranchIndexPath.NODE_ID.getPath(), doc.getNode().getNodeState().value() ).
            addStringValue( BranchIndexPath.STATE.getPath(), doc.getNode().getNodeState().value() ).
            addStringValue( BranchIndexPath.PATH.getPath(), doc.getNode().path().toString() ).
            addInstant( BranchIndexPath.TIMESTAMP.getPath(),
                        doc.getNode().getTimestamp() != null ? doc.getNode().getTimestamp() : Instant.now() ).
            parent( new NodeVersionDocumentId( doc.getNode().id(), doc.getNodeVersionId() ).toString() ).
            routing( doc.getNode().id().toString() ).
            build();

        return StoreRequest.create().
            id( new BranchDocumentId( doc.getNode().id(), context.getBranch() ).toString() ).
            nodePath( doc.getNode().path() ).
            forceRefresh( false ).
            settings( StorageSettings.create().
                storageName( StoreStorageName.from( context.getRepositoryId() ) ).
                storageType( StaticStorageType.BRANCH ).
                build() ).
            data( data ).
            build();
    }


}
