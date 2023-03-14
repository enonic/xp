package com.enonic.xp.repo.impl.node.json;

import com.enonic.xp.security.acl.AccessControlList;

public final class NodeVersionAccessControl
{
    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    public NodeVersionAccessControl( final AccessControlList permissions, final boolean inheritPermissions )
    {
        this.permissions = permissions;
        this.inheritPermissions = inheritPermissions;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
    }
}
