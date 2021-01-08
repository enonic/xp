package com.enonic.xp.core.impl.app;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

class ApplicationConstants
{
    private static final PrincipalKey APPLICATION_SUPER_USER_KEY = PrincipalKey.ofSuperUser();

    private static final User APPLICATION_SUPER_USER = User.create().key( APPLICATION_SUPER_USER_KEY ).login( "node" ).build();

    static final AuthenticationInfo APPLICATION_SU_AUTH_INFO = AuthenticationInfo.create().
        principals( APPLICATION_SUPER_USER_KEY, RoleKeys.ADMIN ).
        user( APPLICATION_SUPER_USER ).
        build();
}
