package com.enonic.xp.repository;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public final class RepositoryConstants
{
    public static final NodePath REPOSITORY_STORAGE_PARENT_PATH = new NodePath( NodePath.ROOT, NodeName.from( "repository" ) );

    public static final ChildOrder DEFAULT_CHILD_ORDER = ChildOrder.defaultOrder();

    private static final AccessControlEntry AUTHENTICATED_READ = AccessControlEntry.create().
        allow( Permission.READ ).
        principal( RoleKeys.AUTHENTICATED ).
        build();

    private static final AccessControlEntry ADMIN_FULL_ACCESS = AccessControlEntry.create().
        allowAll().
        principal( RoleKeys.ADMIN ).
        build();

    public static final AccessControlList DEFAULT_REPO_PERMISSIONS = AccessControlList.of( ADMIN_FULL_ACCESS, AUTHENTICATED_READ );

    public static final Branch MASTER_BRANCH = Branch.from( "master" );


    private RepositoryConstants()
    {
    }
}
