package com.enonic.wem.repo.internal.elasticsearch.branch;

import java.time.Instant;

import com.enonic.wem.repo.internal.branch.BranchContext;
import com.enonic.wem.repo.internal.branch.BranchDocumentId;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageData;
import com.enonic.wem.repo.internal.storage.StorageDocument;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.wem.repo.internal.version.NodeVersionDocumentId;

public class BranchStorageDocFactory
{
    public static StorageDocument create( final StoreBranchDocument doc, final BranchContext context )
    {
        final StorageData data = StorageData.create().
            addStringValue( BranchIndexPath.VERSION_ID.getPath(), doc.getNodeVersionId().toString() ).
            addStringValue( BranchIndexPath.BRANCH_NAME.getPath(), context.getBranch().getName() ).
            addStringValue( BranchIndexPath.NODE_ID.getPath(), doc.getNode().getNodeState().value() ).
            addStringValue( BranchIndexPath.STATE.getPath(), doc.getNode().getNodeState().value() ).
            addStringValue( BranchIndexPath.PATH.getPath(), doc.getNode().path().toString() ).
            addInstant( BranchIndexPath.TIMESTAMP.getPath(),
                        doc.getNode().getTimestamp() != null ? doc.getNode().getTimestamp() : Instant.now() ).
            build();

        final BranchDocumentId branchDocumentId = new BranchDocumentId( doc.getNode().id(), context.getBranch() );

        return StorageDocument.create().
            id( branchDocumentId.toString() ).
            settings( StorageSettings.create().
                storageName( StoreStorageName.from( context.getRepositoryId() ) ).
                storageType( StaticStorageType.BRANCH ).
                forceRefresh( true ).
                parent( new NodeVersionDocumentId( doc.getNode().id(), doc.getNodeVersionId() ).toString() ).
                routing( doc.getNode().id().toString() ).
                build() ).
            data( data ).
            build();
    }


}
