package com.enonic.wem.admin.rest.resource.security.json;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.CreateUserParams;
import com.enonic.wem.api.security.PrincipalKey;

public final class CreateUserJson
{
    private final CreateUserParams createUserParams;

    private final String password;

    @JsonCreator
    public CreateUserJson( @JsonProperty("key") final String userKey, @JsonProperty("displayName") final String displayName,
                           @JsonProperty("email") final String email, @JsonProperty("login") final String login,
                           @JsonProperty("password") final String password )
    {
        final PrincipalKey principalKey = PrincipalKey.from( userKey );
        this.createUserParams = CreateUserParams.create().
            userKey( principalKey ).
            displayName( displayName ).
            email( email ).
            login( login ).
            build();
        this.password = password;
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
}
