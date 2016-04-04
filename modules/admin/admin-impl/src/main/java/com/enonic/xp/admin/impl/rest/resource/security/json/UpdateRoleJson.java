package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.UpdateRoleParams;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static java.util.stream.Collectors.toList;

public final class UpdateRoleJson
{
    private final UpdateRoleParams updateRoleParams;

    private final PrincipalKeys addMembers;

    private final PrincipalKeys removeMembers;

    private final String description;

    @JsonCreator
    public UpdateRoleJson( @JsonProperty("key") final String userKey, @JsonProperty("displayName") final String displayName,
                           @JsonProperty("addMembers") final List<String> addMembers,
                           @JsonProperty("removeMembers") final List<String> removeMembers,
                           @JsonProperty("description") final String description )
    {
        final PrincipalKey principalKey = PrincipalKey.from( userKey );
        this.updateRoleParams = UpdateRoleParams.create().
            roleKey( principalKey ).
            displayName( displayName ).
            description( description ).
            build();
        this.addMembers = PrincipalKeys.from( addMembers.stream().map( PrincipalKey::from ).collect( toList() ) );
        this.removeMembers = PrincipalKeys.from( removeMembers.stream().map( PrincipalKey::from ).collect( toList() ) );
        this.description = description;
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
