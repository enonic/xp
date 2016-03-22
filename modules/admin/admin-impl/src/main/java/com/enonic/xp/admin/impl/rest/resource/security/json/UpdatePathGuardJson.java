package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.PathGuardKey;
import com.enonic.xp.security.UpdatePathGuardParams;
import com.enonic.xp.security.UserStoreKey;

public final class UpdatePathGuardJson
{
    private final UpdatePathGuardParams updatePathGuardParams;

    @JsonCreator
    public UpdatePathGuardJson( @JsonProperty("key") final String key, @JsonProperty("displayName") final String displayName,
                                @JsonProperty("description") final String description,
                                @JsonProperty("userStoreKey") final String userStoreKey, @JsonProperty("paths") final String[] paths )
    {
        this.updatePathGuardParams = UpdatePathGuardParams.create().
            key( PathGuardKey.from( key ) ).
            displayName( displayName ).
            description( description ).
            userStoreKey( userStoreKey == null ? null : UserStoreKey.from( userStoreKey ) ).
            addPaths( paths ).
            build();
    }

    @JsonIgnore
    public UpdatePathGuardParams getUpdatePathGuardParams()
    {
        return updatePathGuardParams;
    }
}
