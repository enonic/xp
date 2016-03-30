package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

public final class UserStoresJson
{
    private final List<UserStoreJson> userStoreJsonList;

    public UserStoresJson( final List<UserStoreJson> userStoreJsonList )
    {
        this.userStoreJsonList = userStoreJsonList;
    }

    public List<UserStoreJson> getUserStores()
    {
        return userStoreJsonList;
    }
}