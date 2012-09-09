package com.enonic.wem.api.userstore.connector;

public final class UserStoreConnector
{
    private final String name;

    public UserStoreConnector( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
