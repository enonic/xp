package com.enonic.wem.api.userstore;

import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.statistics.UserStoreStatistics;

public final class UserStore
{
    public final UserStoreName name;

    private String connectorName;

    private UserStoreConnector connector;

    private UserStoreConfig config;

    private UserStoreStatistics statistics;

    public UserStore( final UserStoreName name )
    {
        this.name = name;
    }

    public UserStoreName getName()
    {
        return this.name;
    }

    public String getConnectorName()
    {
        return connectorName;
    }

    public void setConnectorName( final String connectorName )
    {
        this.connectorName = connectorName;
    }

    public UserStoreConnector getConnector()
    {
        return connector;
    }

    public void setConnector( final UserStoreConnector connector )
    {
        this.connector = connector;
    }

    public UserStoreConfig getConfig()
    {
        return config;
    }

    public void setConfig( final UserStoreConfig config )
    {
        this.config = config;
    }

    public UserStoreStatistics getStatistics()
    {
        return statistics;
    }

    public void setStatistics( final UserStoreStatistics statistics )
    {
        this.statistics = statistics;
    }
}
