package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.enonic.xp.security.Principal;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@SuppressWarnings("UnusedDeclaration")
public class PrincipalJson
{
    private final String principalKey;

    private final String displayName;

    private final Instant modifiedTime;

    private final String description;

    public PrincipalJson( final Principal principal )
    {
        this.principalKey = principal.getKey().toString();
        this.displayName = principal.getDisplayName();
        this.modifiedTime = principal.getModifiedTime();
        this.description = principal.getDescription();
    }

    @JsonCreator
    public PrincipalJson( @JsonProperty("key") final String key,
                          @JsonProperty("displayName") final String displayName,
                          @JsonProperty("description") final String description  )
    {
        this.principalKey = key;
        this.displayName = displayName;
        this.modifiedTime = null;
        this.description = description;
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

    public String getDescription()
    {
        return description;
    }

}
