package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.UpdateUserStoreParams;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.acl.UserStoreAccessControlEntry;
import com.enonic.wem.api.security.acl.UserStoreAccessControlList;

public final class UpdateUserStoreJson
{
    private final UpdateUserStoreParams updateUserStoreParams;

    @JsonCreator
    public UpdateUserStoreJson( @JsonProperty("key") final String userStoreKey, @JsonProperty("displayName") final String displayName,
                                @JsonProperty("permissions") final List<UserStoreAccessControlEntryJson> aclEntries )
    {
        final UserStoreAccessControlEntry[] userStoreAclEntries = aclEntries == null ? null : aclEntries.stream().
            map( UserStoreAccessControlEntryJson::getEntry ).
            toArray( UserStoreAccessControlEntry[]::new );

        final UserStoreAccessControlList permissions = UserStoreAccessControlList.of( userStoreAclEntries );
        this.updateUserStoreParams = UpdateUserStoreParams.create().
            key( new UserStoreKey( userStoreKey ) ).
            displayName( displayName ).
            permissions( permissions ).
            build();
    }

    @JsonIgnore
    public UpdateUserStoreParams getUpdateUserStoreParams()
    {
        return updateUserStoreParams;
    }
}
