package com.enonic.xp.security;

public final class RoleKeys
{
    private RoleKeys()
    {
    }

    public static final String ADMIN_ID = "system.admin";

    public static final PrincipalKey ADMIN = PrincipalKey.ADMIN_ROLE;

    public static final PrincipalKey EVERYONE = PrincipalKey.EVERYONE_ROLE;

    public static final PrincipalKey AUTHENTICATED = PrincipalKey.AUTHENTICATED_ROLE;

    public static final String ADMIN_LOGIN_ID = "system.admin.login";

    public static final PrincipalKey ADMIN_LOGIN = PrincipalKey.ofRole( ADMIN_LOGIN_ID );

    public static final String AUDIT_LOG_ID = "system.auditlog";

    public static final String CONTENT_MANAGER_ADMIN_ID = "cms.admin";

    public static final PrincipalKey AUDIT_LOG = PrincipalKey.ofRole( AUDIT_LOG_ID );

    public static final PrincipalKey USER_MANAGER_APP = PrincipalKey.ofRole( "system.user.app" );

    public static final PrincipalKey USER_MANAGER_ADMIN = PrincipalKey.ofRole( "system.user.admin" );

    /**
     * Legacy role id retained for backwards compatibility with existing installs and external apps that grant it.
     * Superseded by the per-project role hierarchy ({@code cms.project.<name>.{owner,editor,author,contributor,viewer}}).
     * New code must not use it.
     */
    @Deprecated
    public static final String CONTENT_MANAGER_APP_ID = "cms.cm.app";

    /**
     * Legacy role retained for backwards compatibility with existing installs and external apps that grant it.
     * Superseded by the per-project role hierarchy ({@code cms.project.<name>.{owner,editor,author,contributor,viewer}}).
     * New code must not use it.
     */
    @Deprecated
    public static final PrincipalKey CONTENT_MANAGER_APP = PrincipalKey.ofRole( CONTENT_MANAGER_APP_ID );

    public static final PrincipalKey CONTENT_MANAGER_EXPERT = PrincipalKey.ofRole( "cms.expert" );

    public static final PrincipalKey CONTENT_MANAGER_ADMIN = PrincipalKey.ofRole( CONTENT_MANAGER_ADMIN_ID );

    public static final PrincipalKey SCHEMA_ADMIN = PrincipalKey.ofRole( "system.schema.admin" );

}
