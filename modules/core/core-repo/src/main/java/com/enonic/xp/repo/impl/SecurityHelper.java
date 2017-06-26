package com.enonic.xp.repo.impl;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class SecurityHelper
{

    public static boolean isAdmin()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return authInfo.hasRole( RoleKeys.ADMIN );
    }

}
