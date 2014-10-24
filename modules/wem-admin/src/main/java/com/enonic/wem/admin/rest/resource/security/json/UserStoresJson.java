package com.enonic.wem.admin.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStores;

public final class UserStoresJson
{
    private final List<UserStoreJson> userStoresJson;

    public UserStoresJson( final UserStores userStores )
    {
        this.userStoresJson = new ArrayList<>();
        if ( userStores != null )
        {
            for ( UserStore userStore : userStores )
            {
                userStoresJson.add( new UserStoreJson( userStore ) );
            }
        }
    }

    public List<UserStoreJson> getUserStores()
    {
        return userStoresJson;
    }
}
