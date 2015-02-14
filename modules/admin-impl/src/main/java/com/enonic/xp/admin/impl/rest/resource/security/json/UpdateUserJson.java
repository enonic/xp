package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.core.security.PrincipalKey;
import com.enonic.xp.core.security.PrincipalKeys;
import com.enonic.xp.core.security.UpdateUserParams;

import static java.util.stream.Collectors.toList;

public final class UpdateUserJson
{
    private final UpdateUserParams updateUserParams;

    private final PrincipalKeys addMemberships;

    private final PrincipalKeys removeMemberships;

    @JsonCreator
    public UpdateUserJson( @JsonProperty("key") final String userKey, @JsonProperty("displayName") final String displayName,
                           @JsonProperty("email") final String email, @JsonProperty("login") final String login,
                           @JsonProperty("addMemberships") final List<String> addMemberships,
                           @JsonProperty("removeMemberships") final List<String> removeMemberships )
    {
        final PrincipalKey principalKey = PrincipalKey.from( userKey );
        this.updateUserParams = UpdateUserParams.create().
            userKey( principalKey ).
            displayName( displayName ).
            email( email ).
            login( login ).
            build();

        this.addMemberships = PrincipalKeys.from( addMemberships.stream().map( PrincipalKey::from ).collect( toList() ) );
        this.removeMemberships = PrincipalKeys.from( removeMemberships.stream().map( PrincipalKey::from ).collect( toList() ) );
    }

    @JsonIgnore
    public UpdateUserParams getUpdateUserParams()
    {
        return updateUserParams;
    }

    @JsonIgnore
    public PrincipalKeys getAddMemberships()
    {
        return addMemberships;
    }

    @JsonIgnore
    public PrincipalKeys getRemoveMemberships()
    {
        return removeMemberships;
    }
}
