package com.enonic.wem.web.rest.userstore;


import java.util.ArrayList;
import java.util.List;

public class UserStoreConfigsModel
{
    private int total;

    private List<UserStoreConfigModel> userStoreConfigs;

    public UserStoreConfigsModel()
    {
        this.userStoreConfigs = new ArrayList<UserStoreConfigModel>();
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public List<UserStoreConfigModel> getUserStoreConfigs()
    {
        return userStoreConfigs;
    }

    public void setUserStoreConfigs( List<UserStoreConfigModel> userStoreConfigs )
    {
        this.userStoreConfigs = userStoreConfigs;
    }

    public void addUserStoreConfig( UserStoreConfigModel userStoreConfig )
    {
        this.userStoreConfigs.add( userStoreConfig );
    }
}
