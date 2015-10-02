package com.enonic.xp.repo.impl.branch.storage;

import java.time.Instant;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.node.Node;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.branch.BranchDocumentId;
import com.enonic.xp.repo.impl.branch.StoreBranchDocument;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageData;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repo.impl.version.NodeVersionDocumentId;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.acl.Permission;

class BranchStorageRequestFactory
{
    public static StoreRequest create( final StoreBranchDocument doc, final InternalContext context )
    {
        final Node node = doc.getNode();

        final PrincipalKeys principalsWithRead = node.getPermissions().getPrincipalsWithPermission( Permission.READ );

        final List<String> keysAsStrings = Lists.newArrayList();
        principalsWithRead.forEach( ( key ) -> keysAsStrings.add( key.toString() ) );

        final StorageData data = StorageData.create().
            add( BranchIndexPath.VERSION_ID.getPath(), doc.getNodeVersionId().toString() ).
            add( BranchIndexPath.BRANCH_NAME.getPath(), context.getBranch().getName() ).
            add( BranchIndexPath.NODE_ID.getPath(), node.id().toString() ).
            add( BranchIndexPath.STATE.getPath(), node.getNodeState().value() ).
            add( BranchIndexPath.PATH.getPath(), node.path().toString() ).
            add( BranchIndexPath.TIMESTAMP.getPath(), node.getTimestamp() != null ? node.getTimestamp() : Instant.now() ).
            build();

        return StoreRequest.create().
            id( new BranchDocumentId( node.id(), context.getBranch() ).toString() ).
            nodePath( node.path() ).
            forceRefresh( false ).
            settings( StorageSettings.create().
                storageName( StoreStorageName.from( context.getRepositoryId() ) ).
                storageType( StaticStorageType.BRANCH ).
                build() ).
            data( data ).
            parent( new NodeVersionDocumentId( node.id(), doc.getNodeVersionId() ).toString() ).
            routing( node.id().toString() ).
            build();
    }


}
