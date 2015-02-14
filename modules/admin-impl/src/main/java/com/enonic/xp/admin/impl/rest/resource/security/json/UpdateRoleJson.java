package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.core.security.PrincipalKey;
import com.enonic.xp.core.security.PrincipalKeys;
import com.enonic.xp.core.security.UpdateRoleParams;

import static java.util.stream.Collectors.toList;

public final class UpdateRoleJson
{
    private final UpdateRoleParams updateRoleParams;

    private final PrincipalKeys addMembers;

    private final PrincipalKeys removeMembers;

    @JsonCreator
    public UpdateRoleJson( @JsonProperty("key") final String userKey, @JsonProperty("displayName") final String displayName,
                           @JsonProperty("addMembers") final List<String> addMembers,
                           @JsonProperty("removeMembers") final List<String> removeMembers )
    {
        final PrincipalKey principalKey = PrincipalKey.from( userKey );
        this.updateRoleParams = UpdateRoleParams.create().
            roleKey( principalKey ).
            displayName( displayName ).
            build();
        this.addMembers = PrincipalKeys.from( addMembers.stream().map( PrincipalKey::from ).collect( toList() ) );
        this.removeMembers = PrincipalKeys.from( removeMembers.stream().map( PrincipalKey::from ).collect( toList() ) );
    }

    @JsonIgnore
    public UpdateRoleParams getUpdateRoleParams()
    {
        return updateRoleParams;
    }

    @JsonIgnore
    public PrincipalKeys getAddMembers()
    {
        return addMembers;
    }

    @JsonIgnore
    public PrincipalKeys getRemoveMembers()
    {
        return removeMembers;
    }
}
