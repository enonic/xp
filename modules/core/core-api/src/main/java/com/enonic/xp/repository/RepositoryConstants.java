package com.enonic.xp.repository;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public class RepositoryConstants
{
    public static final NodePath REPOSITORY_STORAGE_PARENT_PATH = NodePath.create( NodePath.ROOT, "repository" ).build();

    public static final ChildOrder DEFAULT_CHILD_ORDER = ChildOrder.defaultOrder();

    private static final AccessControlEntry authenticatedRead = AccessControlEntry.create().
        allow( Permission.READ ).
        principal( RoleKeys.AUTHENTICATED ).
        build();

    private static final AccessControlEntry adminFullAccess = AccessControlEntry.create().
        allowAll().
        principal( RoleKeys.ADMIN ).
        build();

    public static final AccessControlList DEFAULT_REPO_PERMISSIONS = AccessControlList.of( adminFullAccess, authenticatedRead );

    public static final Branch MASTER_BRANCH = Branch.from( "master" );


}
