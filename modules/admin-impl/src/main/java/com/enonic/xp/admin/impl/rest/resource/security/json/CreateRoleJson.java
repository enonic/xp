package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.core.security.CreateRoleParams;
import com.enonic.xp.core.security.PrincipalKey;
import com.enonic.xp.core.security.PrincipalKeys;

import static java.util.stream.Collectors.toList;

public final class CreateRoleJson
{
    private final CreateRoleParams createRoleParams;

    private final PrincipalKeys members;

    @JsonCreator
    public CreateRoleJson( @JsonProperty("key") final String userKey, @JsonProperty("displayName") final String displayName,
                           @JsonProperty("members") final List<String> members )
    {
        final PrincipalKey principalKey = PrincipalKey.from( userKey );
        this.createRoleParams = CreateRoleParams.create().
            roleKey( principalKey ).
            displayName( displayName ).
            build();
        this.members = PrincipalKeys.from( members.stream().map( PrincipalKey::from ).collect( toList() ) );
    }

    @JsonIgnore
    public CreateRoleParams getCreateRoleParams()
    {
        return createRoleParams;
    }

    @JsonIgnore
    public PrincipalKeys getMembers()
    {
        return members;
    }
}
