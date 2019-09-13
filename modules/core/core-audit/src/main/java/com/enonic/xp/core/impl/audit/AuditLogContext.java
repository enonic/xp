package com.enonic.xp.core.impl.audit;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class AuditLogContext
{
    private static final User SUPER_USER = User.create().
        key( PrincipalKey.ofSuperUser() ).
        login( PrincipalKey.ofSuperUser().getId() ).
        build();

    private static final AuthenticationInfo ADMIN = AuthenticationInfo.create().
        principals( RoleKeys.ADMIN ).
        user( SUPER_USER ).
        build();

    private static ContextBuilder contextBuilder()
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( AuditLogConstants.AUDIT_LOG_REPO_ID ).
            branch( AuditLogConstants.AUDIT_LOG_BRANCH );
    }

    public static Context createAdminContext()
    {
        return contextBuilder().authInfo( ADMIN ).build();
    }

    public static Context createContext()
    {
        return contextBuilder().build();
    }
}
