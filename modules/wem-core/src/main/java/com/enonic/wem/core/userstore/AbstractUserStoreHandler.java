package com.enonic.wem.core.userstore;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.content.ContentTypeNodeTranslator;

public abstract class AbstractUserStoreHandler<T extends Command>
    extends CommandHandler<T>
{
    protected UserStoreConnector getUserStoreConnector( final UserStoreName userStoreName )
    {
        // TODO: Implement real logic here when we get UserStoreConnector management.
        return null;
    }
}
