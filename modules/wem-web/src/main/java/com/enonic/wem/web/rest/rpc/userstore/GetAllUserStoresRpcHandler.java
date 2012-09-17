package com.enonic.wem.web.rest.rpc.userstore;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
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
        final UserStores userStores = this.client.execute( Commands.userStore().get().names( userStoreNames ) );
        context.setResult( new GetAllUserStoresJsonResult( userStores ) );
    }
}
