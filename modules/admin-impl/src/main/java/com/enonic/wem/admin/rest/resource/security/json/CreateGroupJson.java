package com.enonic.wem.admin.rest.resource.security.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.CreateGroupParams;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;

import static java.util.stream.Collectors.toList;

public final class CreateGroupJson
{
    private final CreateGroupParams createGroupParams;

    private final PrincipalKeys members;

    @JsonCreator
    public CreateGroupJson( @JsonProperty("key") final String userKey, @JsonProperty("displayName") final String displayName,
                            @JsonProperty("members") final List<String> members )
    {
        final PrincipalKey principalKey = PrincipalKey.from( userKey );
        this.createGroupParams = CreateGroupParams.create().
            groupKey( principalKey ).
            displayName( displayName ).
            build();
        this.members = PrincipalKeys.from( members.stream().map( PrincipalKey::from ).collect( toList() ) );
    }

    @JsonIgnore
    public CreateGroupParams getCreateGroupParams()
    {
        return createGroupParams;
    }

    @JsonIgnore
    public PrincipalKeys getMembers()
    {
        return members;
    }
}
