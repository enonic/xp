package com.enonic.xp.security;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public final class SecurityConstants
{
    public static final Branch BRANCH_SECURITY = SystemConstants.BRANCH_SYSTEM;

    static final String ROLES_NODE_NAME = "roles";

    static final String PRINCIPAL_KEY_SEPARATOR = ":";

    private SecurityConstants()
    {
    }
}
