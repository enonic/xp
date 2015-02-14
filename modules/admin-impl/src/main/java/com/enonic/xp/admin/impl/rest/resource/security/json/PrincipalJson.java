package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.Principal;

@SuppressWarnings("UnusedDeclaration")
public class PrincipalJson
{
    private final String principalKey;

    private final String displayName;

    private final Instant modifiedTime;

    public PrincipalJson( final Principal principal )
    {
        this.principalKey = principal.getKey().toString();
        this.displayName = principal.getDisplayName();
        this.modifiedTime = principal.getModifiedTime();
    }

    @JsonCreator
    public PrincipalJson( @JsonProperty("key") final String key, @JsonProperty("displayName") final String displayName )
    {
        this.principalKey = key;
        this.displayName = displayName;
        this.modifiedTime = null;
    }

    public String getKey()
    {
        return this.principalKey;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public Instant getModifiedTime()
    {
        return this.modifiedTime;
    }

}
