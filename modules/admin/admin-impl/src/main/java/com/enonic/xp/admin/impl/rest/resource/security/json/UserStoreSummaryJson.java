package com.enonic.xp.admin.impl.rest.resource.security.json;

import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreAuthConfig;

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

    public UserStoreAuthConfigJson getAuthApplication()
    {
        final UserStoreAuthConfig authConfig = userStore.getAuthConfig();
        return UserStoreAuthConfigJson.toJson( authConfig );
    }
}
