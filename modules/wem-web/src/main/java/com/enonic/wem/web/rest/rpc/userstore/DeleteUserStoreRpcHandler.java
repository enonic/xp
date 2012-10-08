package com.enonic.wem.web.rest.rpc.userstore;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public class DeleteUserStoreRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteUserStoreRpcHandler()
    {
        super( "userstore_delete" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String[] names = context.param( "name" ).required().asStringArray();
        final UserStoreNames userStoreNames = UserStoreNames.from( names );
        final Integer userStoresDeleted = client.execute( Commands.userStore().delete().names( userStoreNames ) );
        context.setResult( new DeleteUserStoreJsonResult( userStoresDeleted ) );
    }
}
