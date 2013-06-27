package com.enonic.wem.admin.rpc.userstore;


import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;


public final class GetAllUserStoresRpcHandler
    extends AbstractDataRpcHandler
{
    public GetAllUserStoresRpcHandler()
    {
        super( "userstore_getAll" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final UserStoreNames userStoreNames = this.client.execute( Commands.userStore().findAll() );
        final UserStores userStores = this.client.execute(
            Commands.userStore().get().names( userStoreNames ).includeConfig().includeConnector().includeStatistics() );
        context.setResult( new GetAllUserStoresJsonResult( userStores ) );
    }
}
