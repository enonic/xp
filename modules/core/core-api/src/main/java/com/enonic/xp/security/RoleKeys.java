package com.enonic.xp.security;

import com.google.common.annotations.Beta;

@Beta
public final class RoleKeys
{
    private RoleKeys()
    {
    }

    public static final String ADMIN_ID = "system.admin";

    public static final PrincipalKey ADMIN = PrincipalKey.ofRole( ADMIN_ID );

    public static final PrincipalKey EVERYONE = PrincipalKey.ofRole( "system.everyone" );

    public static final PrincipalKey AUTHENTICATED = PrincipalKey.ofRole( "system.authenticated" );

    public static final String ADMIN_LOGIN_ID = "system.admin.login";

    public static final PrincipalKey ADMIN_LOGIN = PrincipalKey.ofRole( ADMIN_LOGIN_ID );

    public static final PrincipalKey USER_MANAGER_APP = PrincipalKey.ofRole( "system.user.app" );

    public static final PrincipalKey USER_MANAGER_ADMIN = PrincipalKey.ofRole( "system.user.admin" );

    public static final PrincipalKey CONTENT_MANAGER_APP = PrincipalKey.ofRole( "cms.cm.app" );

    public static final PrincipalKey CONTENT_MANAGER_ADMIN = PrincipalKey.ofRole( "cms.admin" );

}
