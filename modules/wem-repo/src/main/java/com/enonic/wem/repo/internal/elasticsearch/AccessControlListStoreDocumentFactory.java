package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.index.IndexDocumentItemPath;
import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
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

    private static IndexDocumentItemPath getPathForPermission( final Permission permission )
    {
        switch ( permission )
        {
            case READ:
                return IndexPaths.PERMISSIONS_READ_PATH;
            case MODIFY:
                return IndexPaths.PERMISSIONS_MODIFY_PATH;
            case CREATE:
                return IndexPaths.PERMISSIONS_CREATE_PATH;
            case DELETE:
                return IndexPaths.PERMISSIONS_DELETE_PATH;
            case PUBLISH:
                return IndexPaths.PERMISSIONS_PUBLISH_PATH;
            case READ_PERMISSIONS:
                return IndexPaths.PERMISSIONS_READ_PERMISSIONS_PATH;
            case WRITE_PERMISSIONS:
                return IndexPaths.PERMISSIONS_WRITE_PERMISSIONS_PATH;

        }

        throw new IllegalArgumentException( "Unknown permission: " + permission + " for indexing" );
    }

}
