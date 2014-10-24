package com.enonic.wem.admin.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;

public class UserStoresJson
{
    private final UserStores userStores;

    private List<UserStoreJson> userStoresJson;

    public UserStoresJson( final UserStores userStores )
    {
        this.userStores = userStores;

        if ( userStores != null )
        {
            userStoresJson = new ArrayList<>();
            List<UserStore> list = userStores.getList();
            for ( UserStore userStore : list )
            {
                userStoresJson.add( new UserStoreJson( userStore ) );
            }
        }
        else
        {
            userStoresJson = null;
        }
    }

    @JsonCreator
    public UserStoresJson( final List<UserStoreJson> userStoresJson )
    {

        this.userStoresJson = userStoresJson;
        List<UserStore> storeList = new ArrayList<>();
        for ( UserStoreJson us : userStoresJson )
        {
            storeList.add( UserStore.newUserStore().displayName( us.getDisplayName() ).key( new UserStoreKey( us.getKey() ) ).build() );
        }

        this.userStores = UserStores.from( storeList );
    }


    public UserStores getUserStores()
    {
        return userStores;
    }
}
