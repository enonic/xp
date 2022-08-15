package com.enonic.xp.core.impl.app;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class VirtualAppContext
{
    private static final User SUPER_USER =
        User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build();

    private static final AuthenticationInfo ADMIN = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( SUPER_USER ).build();

    private VirtualAppContext()
    {

    }

    private static ContextBuilder contextBuilder()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( VirtualAppConstants.VIRTUAL_APP_REPO_ID )
            .branch( VirtualAppConstants.VIRTUAL_APP_BRANCH );
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
