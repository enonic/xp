package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.CreateGroupParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static java.util.stream.Collectors.toList;

public final class CreateGroupJson
{
    @JsonProperty("key")
    public String userKey;

    @JsonProperty("displayName")
    public String displayName;

    @JsonProperty("members")
    public List<String> members;

    @JsonProperty("description")
    public String description;

    public CreateGroupParams toCreateGroupParams()
    {
        final PrincipalKey principalKey = PrincipalKey.from( this.userKey );
        return CreateGroupParams.create().
            groupKey( principalKey ).
            displayName( this.displayName ).
            description( this.description ).
            build();
    }

    public PrincipalKeys toMemberKeys()
    {
        return PrincipalKeys.from( this.members.stream().map( PrincipalKey::from ).collect( toList() ) );
    }
}
