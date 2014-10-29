package com.enonic.wem.admin.rest.resource.content.json;

import com.enonic.wem.api.security.Principal;

public final class PrincipalJson
{
    private final Principal principal;

    public PrincipalJson( final Principal principal )
    {
        this.principal = principal;
    }

    public String getKey()
    {
        return this.principal.getKey().toString();
    }

    public String getDisplayName()
    {
        return this.principal.getDisplayName();
    }
}
