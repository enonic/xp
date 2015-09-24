package com.enonic.xp.repo.impl.entity;

import com.enonic.xp.security.acl.AccessControlList;

interface PermissionsMergingStrategy
{

    /**
     * @param childAcl  permissions of the child node
     * @param parentAcl permissions of the parent node
     * @return resulting permissions after merging child and parent permissions
     */
    AccessControlList mergePermissions( AccessControlList childAcl, AccessControlList parentAcl );

}
