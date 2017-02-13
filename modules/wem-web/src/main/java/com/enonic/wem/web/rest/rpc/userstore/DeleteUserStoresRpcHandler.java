package com.enonic.wem.web.rest.rpc.userstore;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public class DeleteUserStoresRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteUserStoresRpcHandler()
    {
        super( "userstore_delete" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String[] names = context.param( "name" ).required().asStringArray();
        final int userStoresDeleted = this.client.execute( Commands.userStore().delete().names( UserStoreNames.from( names ) ) );
        context.setResult( new DeleteUserStoresJsonResult( userStoresDeleted ) );
    }
}
