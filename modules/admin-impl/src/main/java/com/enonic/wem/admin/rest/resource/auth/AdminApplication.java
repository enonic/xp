package com.enonic.wem.admin.rest.resource.auth;

import com.enonic.wem.api.security.PrincipalKeys;

final class AdminApplication
{
    private final String id;

    private final PrincipalKeys requiredAccess;

    public AdminApplication( final String id, final PrincipalKeys requiredAccess )
    {
        this.id = id;
        this.requiredAccess = requiredAccess;
    }

    public String getId()
    {
        return id;
    }

    public boolean isAccessAllowed( final PrincipalKeys authPrincipals )
    {
        return authPrincipals.stream().anyMatch( requiredAccess::contains );
    }
}
