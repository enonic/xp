package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.UpdateUserStoreParams;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.UserStoreAccessControlEntry;
import com.enonic.xp.security.acl.UserStoreAccessControlList;

public final class UpdateUserStoreJson
{
    private final UpdateUserStoreParams updateUserStoreParams;

    @JsonCreator
    public UpdateUserStoreJson( @JsonProperty("key") final String userStoreKey, @JsonProperty("displayName") final String displayName,
                                @JsonProperty("description") final String description,
                                @JsonProperty("authConfig") final AuthConfigJson authConfigJson,
                                @JsonProperty("permissions") final List<UserStoreAccessControlEntryJson> aclEntries )
    {
        final UserStoreAccessControlEntry[] userStoreAclEntries = aclEntries == null ? null : aclEntries.stream().
            map( UserStoreAccessControlEntryJson::getEntry ).
            toArray( UserStoreAccessControlEntry[]::new );

        final UserStoreAccessControlList permissions = UserStoreAccessControlList.of( userStoreAclEntries );
        this.updateUserStoreParams = UpdateUserStoreParams.create().
            key( UserStoreKey.from( userStoreKey ) ).
            displayName( displayName ).
            description( description ).
            authConfig( authConfigJson == null ? null : authConfigJson.getAuthConfig() ).
            permissions( permissions ).
            build();
    }

    @JsonIgnore
    public UpdateUserStoreParams getUpdateUserStoreParams()
    {
        return updateUserStoreParams;
    }
}
