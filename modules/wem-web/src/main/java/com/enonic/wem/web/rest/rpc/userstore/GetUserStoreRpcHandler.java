package com.enonic.wem.web.rest.rpc.userstore;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.exception.UserStoreNotFoundException;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class GetUserStoreRpcHandler
    extends AbstractDataRpcHandler
{
    public GetUserStoreRpcHandler()
    {
        super( "userstore_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String name = context.param( "name" ).required().asString();
        try
        {
            final UserStores userStores = this.client.execute(
                Commands.userStore().get().names( UserStoreNames.from( name ) ).includeConfig().includeConnector().includeStatistics() );
            final UserStore userStore = userStores.getFirst();
            context.setResult( new GetUserStoreJsonResult( userStore ) );
        }
        catch ( UserStoreNotFoundException e )
        {
            JsonErrorResult result = new JsonErrorResult( "No userstore(s) were found for name [" + name + "]" );
            context.setResult( result );
        }
    }
}
