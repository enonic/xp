package com.enonic.wem.admin.rest.resource.security.json;


import java.time.Instant;

import com.enonic.wem.api.security.Principal;

public class PrincipalJson
{
    private final Principal principal;

    public PrincipalJson( final Principal principal )
    {
        this.principal = principal;
    }

    public String getKey()
    {
        return principal.getKey().toString();
    }

    public String getDisplayName()
    {
        return principal.getDisplayName();
    }

    public Instant getModifiedTime()
    {
        return principal.getModifiedTime();
    }

}
