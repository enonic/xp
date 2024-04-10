package com.enonic.xp.repo.impl.node;

import com.enonic.xp.security.acl.AccessControlList;

sealed interface PermissionsMergingStrategy
    permits DefaultPermissionsMergingStrategy, OverwriteChildPermissionsMergingStrategy, KeepChildPermissionsMergingStrategy
{
    DefaultPermissionsMergingStrategy MERGE = new DefaultPermissionsMergingStrategy();

    OverwriteChildPermissionsMergingStrategy OVERWRITE = new OverwriteChildPermissionsMergingStrategy();

    /**
     * Returns resulting permissions after merging child and parent permissions.
     *
     * @param childAcl  permissions of the child node
     * @param parentAcl permissions of the parent node
     * @return resulting permissions after merging child and parent permissions
     */
    AccessControlList mergePermissions( AccessControlList childAcl, AccessControlList parentAcl );

}
