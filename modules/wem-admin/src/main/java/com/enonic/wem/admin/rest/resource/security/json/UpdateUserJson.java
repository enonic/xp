package com.enonic.wem.admin.rest.resource.security.json;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.UpdateUserParams;

public final class UpdateUserJson
{
    private final UpdateUserParams updateUserParams;

    @JsonCreator
    public UpdateUserJson( @JsonProperty("key") final String userKey, @JsonProperty("displayName") final String displayName,
                           @JsonProperty("email") final String email, @JsonProperty("login") final String login )
    {
        final PrincipalKey principalKey = PrincipalKey.from( userKey );
        this.updateUserParams = UpdateUserParams.create().
            userKey( principalKey ).
            displayName( displayName ).
            email( email ).
            login( login ).
            build();
    }

    @JsonIgnore
    public UpdateUserParams getUpdateUserParams()
    {
        return updateUserParams;
    }

}
