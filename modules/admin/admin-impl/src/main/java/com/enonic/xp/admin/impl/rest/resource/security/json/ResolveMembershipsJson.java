package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static java.util.stream.Collectors.toList;

public final class ResolveMembershipsJson
{
    public PrincipalKeys keys;

    @JsonCreator
    public ResolveMembershipsJson( @JsonProperty("keys") final List<String> keys )
    {
        this.keys = PrincipalKeys.from( keys.stream().map( PrincipalKey::from ).collect( toList() ) );
    }

    public PrincipalKeys getMembers()
    {
        return keys;
    }

}
