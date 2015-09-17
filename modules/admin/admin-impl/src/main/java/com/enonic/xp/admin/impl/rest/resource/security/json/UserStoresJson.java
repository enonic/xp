package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStores;

public final class UserStoresJson
{
    private final List<UserStoreSummaryJson> userStoresJson;

    public UserStoresJson( final UserStores userStores )
    {
        this.userStoresJson = new ArrayList<>();
        if ( userStores != null )
        {
            for ( UserStore userStore : userStores )
            {
                userStoresJson.add( new UserStoreSummaryJson( userStore ) );
            }
        }
    }

    public List<UserStoreSummaryJson> getUserStores()
    {
        return userStoresJson;
    }
}
