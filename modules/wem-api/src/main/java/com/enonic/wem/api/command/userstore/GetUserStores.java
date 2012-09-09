package com.enonic.wem.api.command.userstore;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;

public final class GetUserStores
    extends Command<UserStores>
{
    private UserStoreNames names;

    private boolean includeConfig;

    private boolean includeConnector;

    private boolean includeStatistics;

    public GetUserStores names( final UserStoreNames names )
    {
        this.names = names;
        return this;
    }

    public GetUserStores includeConfig()
    {
        this.includeConfig = true;
        return this;
    }

    public GetUserStores includeConnector()
    {
        this.includeConnector = true;
        return this;
    }

    public GetUserStores includeStatistics()
    {
        this.includeStatistics = true;
        return this;
    }

    public UserStoreNames getNames()
    {
        return this.names;
    }

    public boolean isIncludeConfig()
    {
        return this.includeConfig;
    }

    public boolean isIncludeConnector()
    {
        return this.includeConnector;
    }

    public boolean isIncludeStatistics()
    {
        return this.includeStatistics;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.names, "UserStore names cannot be null" );
    }
}
