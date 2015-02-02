package com.enonic.wem.api.security;

public final class RoleKeys
{
    private RoleKeys()
    {
    }

    public static final PrincipalKey ADMIN = PrincipalKey.ofRole( "system.admin" );

    public static final PrincipalKey EVERYONE = PrincipalKey.ofRole( "system.everyone" );

    public static final PrincipalKey AUTHENTICATED = PrincipalKey.ofRole( "system.authenticated" );

    public static final String ADMIN_LOGIN_ID = "system.admin.login";

    public static final PrincipalKey ADMIN_LOGIN = PrincipalKey.ofRole( ADMIN_LOGIN_ID );

    public static final PrincipalKey USER_MANAGER_APP = PrincipalKey.ofRole( "system.user.app" );

    public static final PrincipalKey CONTENT_MANAGER_APP = PrincipalKey.ofRole( "system.cm.app" );

    public static final PrincipalKey CONTENT_MANAGER = PrincipalKey.ofRole( "system.cm.admin" ); // TODO remove?

}
