package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.CreatePathGuardParams;
import com.enonic.xp.security.PathGuardKey;
import com.enonic.xp.security.UserStoreKey;

public final class CreatePathGuardJson
{
    private final CreatePathGuardParams createPathGuardParams;

    @JsonCreator
    public CreatePathGuardJson( @JsonProperty("key") final String key, @JsonProperty("displayName") final String displayName,
                                @JsonProperty("description") final String description,
                                @JsonProperty("userStoreKey") final String userStoreKey, @JsonProperty("paths") final String[] paths )
    {
        this.createPathGuardParams = CreatePathGuardParams.create().
            key( PathGuardKey.from( key ) ).
            displayName( displayName ).
            description( description ).
            userStoreKey( userStoreKey == null ? null : UserStoreKey.from( userStoreKey ) ).
            addPaths( paths ).
            build();
    }

    @JsonIgnore
    public CreatePathGuardParams getCreatePathGuardParams()
    {
        return createPathGuardParams;
    }


}
