package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.UpdatePathGuardParams;

public final class UpdatePathGuardJson
{
    private final UpdatePathGuardParams updatePathGuardParams;

    @JsonCreator
    public UpdatePathGuardJson( @JsonProperty("key") final String key, @JsonProperty("displayName") final String displayName,
                                @JsonProperty("authConfig") final UserStoreAuthConfigJson authConfigJson,
                                @JsonProperty("paths") final String[] paths )
    {
        this.updatePathGuardParams = UpdatePathGuardParams.create().
            key( key ).
            displayName( displayName ).
            authConfig( authConfigJson == null ? null : authConfigJson.getUserStoreAuthConfig() ).
            addPaths( paths ).
            build();
    }

    @JsonIgnore
    public UpdatePathGuardParams getUpdatePathGuardParams()
    {
        return updatePathGuardParams;
    }
}
