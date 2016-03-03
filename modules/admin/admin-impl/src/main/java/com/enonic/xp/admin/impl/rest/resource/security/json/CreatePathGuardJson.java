package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.CreatePathGuardParams;
import com.enonic.xp.security.PathGuardKey;

public final class CreatePathGuardJson
{
    private final CreatePathGuardParams createPathGuardParams;

    @JsonCreator
    public CreatePathGuardJson( @JsonProperty("key") final String key, @JsonProperty("displayName") final String displayName,
                                @JsonProperty("authConfig") final AuthConfigJson authConfigJson,
                                @JsonProperty("paths") final String[] paths )
    {
        this.createPathGuardParams = CreatePathGuardParams.create().
            key( PathGuardKey.from( key ) ).
            displayName( displayName ).
            authConfig( authConfigJson == null ? null : authConfigJson.getAuthConfig() ).
            addPaths( paths ).
            build();
    }

    @JsonIgnore
    public CreatePathGuardParams getCreatePathGuardParams()
    {
        return createPathGuardParams;
    }


}
