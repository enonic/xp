package com.enonic.wem.web.rest.userstore;


import java.util.ArrayList;
import java.util.List;

public class UserStoreConnectorsModel
{
    private int total;

    private List<UserStoreConnectorModel> userStoreConnectors;

    public UserStoreConnectorsModel()
    {
        this.userStoreConnectors = new ArrayList<UserStoreConnectorModel>();
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public List<UserStoreConnectorModel> getUserStoreConnectors()
    {
        return userStoreConnectors;
    }

    public void setUserStoreConnectors( List<UserStoreConnectorModel> userStoreConnectors )
    {
        this.userStoreConnectors = userStoreConnectors;
    }

    public void addUserStoreConnector( UserStoreConnectorModel userStoreConnector )
    {
        this.userStoreConnectors.add( userStoreConnector );
    }
}
