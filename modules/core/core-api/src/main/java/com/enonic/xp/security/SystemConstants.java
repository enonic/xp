package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

@Beta
public final class SystemConstants
{
    public static final BranchId BRANCH_ID_SYSTEM = BranchId.create().
        value( "master" ).
        build();

    public static final Repository SYSTEM_REPO = Repository.create().
        id( RepositoryId.from( "system-repo" ) ).
        build();

    private static final AccessControlEntry authenticatedRead = AccessControlEntry.create().
        allow( Permission.READ ).
        principal( RoleKeys.AUTHENTICATED ).
        build();

    private static final AccessControlEntry adminFullAccess = AccessControlEntry.create().
        allowAll().
        principal( RoleKeys.ADMIN ).
        build();

    public static final AccessControlList SYSTEM_REPO_DEFAULT_ACL = AccessControlList.of( adminFullAccess, authenticatedRead );


}
