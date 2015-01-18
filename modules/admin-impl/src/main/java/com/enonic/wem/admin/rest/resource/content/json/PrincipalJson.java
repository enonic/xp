package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.Principal;

public final class PrincipalJson
{
    private final String principalKey;

    private final String displayName;

    public PrincipalJson( final Principal principal )
    {
        this.principalKey = principal.getKey().toString();
        this.displayName = principal.getDisplayName();
    }

    @JsonCreator
    public PrincipalJson( @JsonProperty("key") final String key, @JsonProperty("displayName") final String displayName )
    {
        this.principalKey = key;
        this.displayName = displayName;
    }

    public String getKey()
    {
        return this.principalKey;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }
}
