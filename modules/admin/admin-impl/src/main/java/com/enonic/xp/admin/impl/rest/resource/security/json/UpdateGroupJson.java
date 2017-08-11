package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.UpdateGroupParams;

import static java.util.stream.Collectors.toList;

public final class UpdateGroupJson
{
    private final UpdateGroupParams updateGroupParams;

    private final PrincipalKeys addMembers;

    private final PrincipalKeys removeMembers;

    private final PrincipalKeys addMemberships;

    private final PrincipalKeys removeMemberships;

    private final String description;

    @JsonCreator
    public UpdateGroupJson( @JsonProperty("key") final String userKey, @JsonProperty("displayName") final String displayName,
                            @JsonProperty("addMembers") final List<String> addMembers,
                            @JsonProperty("removeMembers") final List<String> removeMembers,
                            @JsonProperty("addMemberships") final List<String> addMemberships,
                            @JsonProperty("removeMemberships") final List<String> removeMemberships,
                            @JsonProperty("description") final String description )
    {
        final PrincipalKey principalKey = PrincipalKey.from( userKey );
        this.updateGroupParams = UpdateGroupParams.create().
            groupKey( principalKey ).
            displayName( displayName ).
            description( description ).
            build();
        this.addMembers = PrincipalKeys.from( addMembers.stream().map( PrincipalKey::from ).collect( toList() ) );
        this.removeMembers = PrincipalKeys.from( removeMembers.stream().map( PrincipalKey::from ).collect( toList() ) );
        this.addMemberships = PrincipalKeys.from( addMemberships.stream().map( PrincipalKey::from ).collect( toList() ) );
        this.removeMemberships = PrincipalKeys.from( removeMemberships.stream().map( PrincipalKey::from ).collect( toList() ) );
        this.description = description;
    }

    @JsonIgnore
    public UpdateGroupParams getUpdateGroupParams()
    {
        return updateGroupParams;
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
