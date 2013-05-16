package com.enonic.wem.core.userstore;


import com.enonic.wem.api.command.userstore.GetUserStoreConnectors;
import com.enonic.wem.api.userstore.connector.UserStoreConnectors;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;


public class GetUserStoreConnectorsHandler
    extends CommandHandler<GetUserStoreConnectors>
{
    public GetUserStoreConnectorsHandler()
    {
        super( GetUserStoreConnectors.class );
    }

    @Override
    public void handle( final CommandContext context, final GetUserStoreConnectors command )
        throws Exception
    {
        command.setResult( UserStoreConnectors.from() );
    }
}
