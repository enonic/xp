package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static java.util.stream.Collectors.toList;

public final class CreateUserJson
{
    @JsonProperty("key")
    public String userKey;

    @JsonProperty("displayName")
    public String displayName;

    @JsonProperty("email")
    public String email;

    @JsonProperty("login")
    public String login;

    @JsonProperty("password")
    public String password;

    @JsonProperty("memberships")
    public List<String> memberships = Collections.emptyList();

    public CreateUserParams toCreateUserParams()
    {
        final PrincipalKey principalKey = PrincipalKey.from( this.userKey );
        return CreateUserParams.create().
            userKey( principalKey ).
            displayName( this.displayName ).
            email( this.email ).
            login( this.login ).
            build();
    }

    @JsonIgnore
    public PrincipalKeys toMembershipKeys()
    {
        return PrincipalKeys.from( this.memberships.stream().map( PrincipalKey::from ).collect( toList() ) );
    }
}
