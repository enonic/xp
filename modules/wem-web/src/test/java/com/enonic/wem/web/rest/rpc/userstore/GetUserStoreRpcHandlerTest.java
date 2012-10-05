package com.enonic.wem.web.rest.rpc.userstore;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.userstore.GetUserStores;
import com.enonic.wem.api.exception.UserStoreNotFoundException;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;

public class GetUserStoreRpcHandlerTest
    extends AbstractUserStoreRpcHandlerTest

{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final GetUserStoreRpcHandler handler = new GetUserStoreRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void getExistingUserStore()
        throws Exception
    {
        final UserStore userStore1 = createUserStore( UserStoreName.from( "enonic" ), "ldap", true, true, true, true );
        final UserStore userStore2 = createUserStore( UserStoreName.from( "system" ), "ad", false, false, false, false );
        final UserStores userStores = UserStores.from( userStore1, userStore2 );

        Mockito.when( client.execute( Mockito.isA( GetUserStores.class ) ) ).thenReturn( userStores );

        testSuccess( createParams( "enonic" ), "userstore_get.json" );
    }

    @Test
    public void getNonExistingUserStore()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetUserStores.class ) ) ).thenThrow(
            new UserStoreNotFoundException( UserStoreName.from( "default" ) ) );

        testSuccess( createParams( "default" ), createNegativeResult( "No userstore(s) were found for name [default]" ) );
    }

    private ObjectNode createParams( String name )
    {
        ObjectNode params = objectNode();
        params.put( "name", name );
        return params;
    }

    private ObjectNode createNegativeResult( String error )
    {
        ObjectNode results = objectNode();
        results.put( "success", false );
        results.put( "error", error );
        return results;
    }

}
