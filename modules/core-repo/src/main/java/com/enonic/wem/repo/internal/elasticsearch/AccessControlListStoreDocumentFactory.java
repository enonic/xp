package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.core.index.IndexPath;
import com.enonic.xp.core.node.NodeIndexPath;
import com.enonic.xp.core.security.PrincipalKey;
import com.enonic.xp.core.security.acl.AccessControlEntry;
import com.enonic.xp.core.security.acl.AccessControlList;
import com.enonic.xp.core.security.acl.Permission;
import com.enonic.wem.repo.internal.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentStringItem;

public class AccessControlListStoreDocumentFactory
{
    static Set<AbstractStoreDocumentItem> create( final AccessControlList accessControlList )
    {
        final Set<AbstractStoreDocumentItem> aclStoreDocumentItems = Sets.newHashSet();

        for ( final AccessControlEntry entry : accessControlList )
        {
            final Iterable<Permission> allowedPermissions = entry.getAllowedPermissions();

            final PrincipalKey principalKey = entry.getPrincipal();

            for ( final Permission permission : allowedPermissions )
            {
                aclStoreDocumentItems.add( new StoreDocumentStringItem( getPathForPermission( permission ), principalKey.toString() ) );
            }
        }

        return aclStoreDocumentItems;
    }

    private static IndexPath getPathForPermission( final Permission permission )
    {
        switch ( permission )
        {
            case READ:
                return NodeIndexPath.PERMISSIONS_READ;
            case MODIFY:
                return NodeIndexPath.PERMISSIONS_MODIFY;
            case CREATE:
                return NodeIndexPath.PERMISSIONS_CREATE;
            case DELETE:
                return NodeIndexPath.PERMISSIONS_DELETE;
            case PUBLISH:
                return NodeIndexPath.PERMISSIONS_PUBLISH;
            case READ_PERMISSIONS:
                return NodeIndexPath.PERMISSIONS_READ_PERMISSION;
            case WRITE_PERMISSIONS:
                return NodeIndexPath.PERMISSIONS_WRITE_PERMISSION;

        }

        throw new IllegalArgumentException( "Unknown permission: " + permission + " for indexing" );
    }

}
