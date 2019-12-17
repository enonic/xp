package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class FetchPrincipalsByKeysJson
{
    @JsonProperty("keys")
    private List<String> keys;

    @JsonProperty("memberships")
    private Boolean resolveMemberships;

    public List<String> getKeys()
    {
        return keys;
    }

    public Boolean getResolveMemberships()
    {
        return resolveMemberships;
    }
}
