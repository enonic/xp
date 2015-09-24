package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.repo.impl.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.xp.repo.impl.elasticsearch.document.StoreDocumentStringItem;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

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
