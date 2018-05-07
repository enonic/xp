package com.enonic.xp.core.impl.app;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

class ApplicationConstants
{
    private static final Branch BRANCH_APPLICATIONS = SystemConstants.BRANCH_SYSTEM;

    private static final Repository APPLICATIONS_REPO = SystemConstants.SYSTEM_REPO;

    private static final PrincipalKey APPLICATION_SUPER_USER_KEY = PrincipalKey.ofSuperUser();

    private static final User APPLICATION_SUPER_USER = User.create().key( APPLICATION_SUPER_USER_KEY ).login( "node" ).build();

    static final Context CONTEXT_APPLICATIONS = ContextBuilder.create().
        branch( BRANCH_APPLICATIONS ).
        repositoryId( APPLICATIONS_REPO.getId() ).
        build();

    static final AuthenticationInfo APPLICATION_SU_AUTH_INFO = AuthenticationInfo.create().
        principals( APPLICATION_SUPER_USER_KEY, RoleKeys.ADMIN ).
        user( APPLICATION_SUPER_USER ).
        build();
}
