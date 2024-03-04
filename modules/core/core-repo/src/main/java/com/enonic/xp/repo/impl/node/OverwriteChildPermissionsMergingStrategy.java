package com.enonic.xp.repo.impl.node;

import com.enonic.xp.security.acl.AccessControlList;

/**
 * <p>
 * Overwrite child permissions with parent permissions.
 * <p>
 * Example:
 * Parent: user1[+create, -modify, -publish],          user2[+create, -modify]
 * Child : user1[+create, +modify, -delete],                                    user3[+create, -modify]
 * ------------------------------------------------------------------------------------------------------
 * Result: user1[+create, -modify, -publish],          user2[+create, -modify]
 */
final class OverwriteChildPermissionsMergingStrategy
    implements PermissionsMergingStrategy
{
    @Override
    public AccessControlList mergePermissions( final AccessControlList childAcl, final AccessControlList parentAcl )
    {
        return parentAcl;
    }
}
