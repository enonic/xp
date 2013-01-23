package com.enonic.wem.core.userstore;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.core.command.CommandHandler;

public abstract class AbstractUserStoreHandler<T extends Command>
    extends CommandHandler<T>
{
    public AbstractUserStoreHandler( final Class<T> type )
    {
        super( type );
    }

    protected UserStoreConnector getUserStoreConnector( final UserStoreName userStoreName )
    {
        // TODO: Implement real logic here when we get UserStoreConnector management.
        return null;
    }
}
