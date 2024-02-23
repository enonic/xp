package com.enonic.xp.repo.impl.node.json;

import com.enonic.xp.security.acl.AccessControlList;

public final class NodeVersionAccessControl
{
    private final AccessControlList permissions;


    public NodeVersionAccessControl( final AccessControlList permissions )
    {
        this.permissions = permissions;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

}
