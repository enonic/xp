package com.enonic.wem.admin.rest.resource.security.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.PrincipalKey;

public final class UpdatePasswordJson
{
    private final PrincipalKey userKey;

    private final String password;

    @JsonCreator
    public UpdatePasswordJson( @JsonProperty("key") final String userKey, @JsonProperty("password") final String password)
    {
        this.userKey = PrincipalKey.from( userKey );
        this.password = password;
    }

    public PrincipalKey getUserKey()
    {
        return this.userKey;
    }

    public String getPassword()
    {
        return this.password;
    }
}
