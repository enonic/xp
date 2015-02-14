package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.CreateUserStoreParams;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.UserStoreAccessControlEntry;
import com.enonic.xp.security.acl.UserStoreAccessControlList;

public final class CreateUserStoreJson
{
    private final CreateUserStoreParams createUserStoreParams;

    @JsonCreator
    public CreateUserStoreJson( @JsonProperty("key") final String userStoreKey, @JsonProperty("displayName") final String displayName,
                                @JsonProperty("permissions") final List<UserStoreAccessControlEntryJson> aclEntries )
    {
        final UserStoreAccessControlEntry[] userStoreAclEntries = aclEntries.stream().map( UserStoreAccessControlEntryJson::getEntry ).
            toArray( UserStoreAccessControlEntry[]::new );

        final UserStoreAccessControlList permissions = UserStoreAccessControlList.of( userStoreAclEntries );
        this.createUserStoreParams = CreateUserStoreParams.create().
            key( new UserStoreKey( userStoreKey ) ).
            displayName( displayName ).
            permissions( permissions ).
            build();
    }

    @JsonIgnore
    public CreateUserStoreParams getCreateUserStoreParams()
    {
        return createUserStoreParams;
    }
}
