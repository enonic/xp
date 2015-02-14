package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.CreateUserParams;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;

import static java.util.stream.Collectors.toList;

public final class CreateUserJson
{
    private final CreateUserParams createUserParams;

    private final String password;

    private final PrincipalKeys memberships;

    @JsonCreator
    public CreateUserJson( @JsonProperty("key") final String userKey, @JsonProperty("displayName") final String displayName,
                           @JsonProperty("email") final String email, @JsonProperty("login") final String login,
                           @JsonProperty("password") final String password, @JsonProperty("memberships") final List<String> memberships )
    {
        final PrincipalKey principalKey = PrincipalKey.from( userKey );
        this.createUserParams = CreateUserParams.create().
            userKey( principalKey ).
            displayName( displayName ).
            email( email ).
            login( login ).
            build();
        this.password = password;
        this.memberships = PrincipalKeys.from( memberships.stream().map( PrincipalKey::from ).collect( toList() ) );
    }

    @JsonIgnore
    public CreateUserParams getCreateUserParams()
    {
        return createUserParams;
    }

    @JsonIgnore
    public String getPassword()
    {
        return password;
    }

    @JsonIgnore
    public PrincipalKeys getMemberships()
    {
        return memberships;
    }
}
