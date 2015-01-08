package com.enonic.wem.api.security;

public final class RoleKeys
{
    private RoleKeys()
    {
    }

    public static final PrincipalKey ENTERPRISE_ADMIN = PrincipalKey.ofRole( "ea" );

    public static final PrincipalKey USER_MANAGER = PrincipalKey.ofRole( "um" );

    public static final PrincipalKey CONTENT_MANAGER = PrincipalKey.ofRole( "cm" );

    public static final PrincipalKey OWNER = PrincipalKey.ofRole( "owner" );

    public static final PrincipalKey EVERYONE = PrincipalKey.ofRole( "everyone" );

    public static final PrincipalKey ADMIN_LOGIN = PrincipalKey.ofRole( "admin-login" );

}
