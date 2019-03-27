package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

@Beta
public final class SystemConstants
{
    public static final Branch BRANCH_SYSTEM = Branch.create().
        value( "master" ).
        build();

    public static final RepositoryId SYSTEM_REPO_ID = RepositoryId.from( "system-repo" );

    public static final Repository SYSTEM_REPO = Repository.create().
        id( SYSTEM_REPO_ID ).
        branches( Branches.from( BRANCH_SYSTEM ) ).
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
