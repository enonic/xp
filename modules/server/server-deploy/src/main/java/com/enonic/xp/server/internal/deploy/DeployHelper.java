package com.enonic.xp.server.internal.deploy;

import java.util.concurrent.Callable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

final class DeployHelper
{
    private static final Branch BRANCH_APPLICATIONS = SystemConstants.BRANCH_SYSTEM;

    private static final RepositoryId APPLICATIONS_REPO_ID = SystemConstants.SYSTEM_REPO_ID;

    private static final PrincipalKey APPLICATION_SUPER_USER_KEY = PrincipalKey.ofSuperUser();

    private static final User APPLICATION_SUPER_USER = User.create().key( APPLICATION_SUPER_USER_KEY ).login( "node" ).build();

    private static final Context CONTEXT_APPLICATIONS = ContextBuilder.create().
        branch( BRANCH_APPLICATIONS ).
        repositoryId( APPLICATIONS_REPO_ID ).
        build();

    private static final AuthenticationInfo APPLICATION_SU_AUTH_INFO = AuthenticationInfo.create().
        principals( APPLICATION_SUPER_USER_KEY, RoleKeys.ADMIN ).
        user( APPLICATION_SUPER_USER ).
        build();

    static <T> T runAsAdmin( final Callable<T> callable )
    {
        return ContextBuilder.from( CONTEXT_APPLICATIONS ).
            authInfo( APPLICATION_SU_AUTH_INFO ).
            build().
            callWith( callable );
    }

    static void runAsAdmin( final Runnable runnable )
    {
        ContextBuilder.from( CONTEXT_APPLICATIONS ).
            authInfo( APPLICATION_SU_AUTH_INFO ).
            build().
            runWith( runnable );
    }
}
