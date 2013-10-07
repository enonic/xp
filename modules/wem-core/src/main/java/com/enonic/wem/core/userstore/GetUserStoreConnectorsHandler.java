package com.enonic.wem.core.userstore;


import com.enonic.wem.api.command.userstore.GetUserStoreConnectors;
import com.enonic.wem.api.userstore.connector.UserStoreConnectors;
import com.enonic.wem.core.command.CommandHandler;


public class GetUserStoreConnectorsHandler
    extends CommandHandler<GetUserStoreConnectors>
{
    @Override
    public void handle()
        throws Exception
    {
        command.setResult( UserStoreConnectors.from() );
    }
}
