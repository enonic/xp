package com.enonic.xp.repo.impl.elasticsearch;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItem;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItemString;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

class AccessControlListStoreDocumentFactory
{
    static List<IndexItem> create( final AccessControlList accessControlList )
    {
        final ImmutableList.Builder<IndexItem> aclStoreDocumentItems = ImmutableList.builder();

        for ( final AccessControlEntry entry : accessControlList )
        {
            final String principalKeyString = entry.getPrincipal().toString();

            for ( final Permission permission : entry.getAllowedPermissions() )
            {
                aclStoreDocumentItems.add( new IndexItemString( getPathForPermission( permission ), principalKeyString ) );
            }
        }

        return aclStoreDocumentItems.build();
    }

    private static IndexPath getPathForPermission( final Permission permission )
    {
        return switch ( permission )
        {
            case READ -> NodeIndexPath.PERMISSIONS_READ;
            case MODIFY -> NodeIndexPath.PERMISSIONS_MODIFY;
            case CREATE -> NodeIndexPath.PERMISSIONS_CREATE;
            case DELETE -> NodeIndexPath.PERMISSIONS_DELETE;
            case PUBLISH -> NodeIndexPath.PERMISSIONS_PUBLISH;
            case READ_PERMISSIONS -> NodeIndexPath.PERMISSIONS_READ_PERMISSION;
            case WRITE_PERMISSIONS -> NodeIndexPath.PERMISSIONS_WRITE_PERMISSION;
        };
    }
}
