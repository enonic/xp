package com.enonic.xp.repo.impl.branch.storage;

import java.time.Instant;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeVersion;
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
        final NodeVersion nodeVersion = doc.getNodeVersion();

        final BranchNodeVersion branchNodeVersion = doc.getBranchNodeVersion();

        final PrincipalKeys principalsWithRead = nodeVersion.getPermissions().getPrincipalsWithPermission( Permission.READ );

        final List<String> keysAsStrings = Lists.newArrayList();
        principalsWithRead.forEach( ( key ) -> keysAsStrings.add( key.toString() ) );

        final StorageData data = StorageData.create().
            add( BranchIndexPath.VERSION_ID.getPath(), branchNodeVersion.getVersionId().toString() ).
            add( BranchIndexPath.BRANCH_NAME.getPath(), context.getBranch().getName() ).
            add( BranchIndexPath.NODE_ID.getPath(), nodeVersion.getId().toString() ).
            add( BranchIndexPath.STATE.getPath(), branchNodeVersion.getNodeState().value() ).
            add( BranchIndexPath.PATH.getPath(), branchNodeVersion.getNodePath().toString() ).
            add( BranchIndexPath.TIMESTAMP.getPath(), nodeVersion.getTimestamp() != null ? nodeVersion.getTimestamp() : Instant.now() ).
            build();

        return StoreRequest.create().
            id( new BranchDocumentId( nodeVersion.getId(), context.getBranch() ).toString() ).
            nodePath( branchNodeVersion.getNodePath() ).
            forceRefresh( false ).
            settings( StorageSettings.create().
                storageName( StoreStorageName.from( context.getRepositoryId() ) ).
                storageType( StaticStorageType.BRANCH ).
                build() ).
            data( data ).
            parent( new NodeVersionDocumentId( nodeVersion.getId(), branchNodeVersion.getVersionId() ).toString() ).
            routing( nodeVersion.getId().toString() ).
            build();
    }


}
