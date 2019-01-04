package com.enonic.xp.admin.impl.rest.resource.security.json;

import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.UserStore;

@SuppressWarnings("UnusedDeclaration")
public class UserStoreSummaryJson
{
    private final UserStore userStore;

    public UserStoreSummaryJson( final UserStore userStore )
    {

        this.userStore = userStore;
    }

    public String getDisplayName()
    {
        return userStore.getDisplayName();
    }

    public String getKey()
    {
        return userStore.getKey().toString();
    }

    public String getDescription()
    {
        return userStore.getDescription();
    }

    public IdProviderConfigJson getIdProviderConfig()
    {
        final IdProviderConfig idProviderConfig = userStore.getIdProviderConfig();
        return IdProviderConfigJson.toJson( idProviderConfig );
    }

}