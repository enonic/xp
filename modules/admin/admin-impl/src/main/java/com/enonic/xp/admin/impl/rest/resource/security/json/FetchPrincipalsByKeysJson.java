package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.CreateGroupParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static java.util.stream.Collectors.toList;

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
