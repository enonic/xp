package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonProperty("memberships")
    public List<String> memberships = Collections.emptyList();

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

    @JsonIgnore
    public PrincipalKeys toMemberKeys()
    {
        return PrincipalKeys.from( this.members.stream().map( PrincipalKey::from ).collect( toList() ) );
    }

    @JsonIgnore
    public PrincipalKeys toMembershipKeys()
    {
        return PrincipalKeys.from( this.memberships.stream().map( PrincipalKey::from ).collect( toList() ) );
    }
}
