package com.enonic.wem.web.rest.rpc.userstore;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.userstore.FindAllUserStores;
import com.enonic.wem.api.command.userstore.GetUserStores;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

public class GetAllUserStoresRpcHandlerTest
    extends AbstractRpcHandlerTest

{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final GetAllUserStoresRpcHandler handler = new GetAllUserStoresRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void getConnectors()
        throws Exception
    {
        final UserStoreName userStoreName1 = UserStoreName.from( "enonic" );
        final UserStoreName userStoreName2 = UserStoreName.from( "system" );
        final UserStoreNames userStoreNames = UserStoreNames.from( userStoreName1, userStoreName2 );
        Mockito.when( client.execute( Mockito.isA( FindAllUserStores.class ) ) ).thenReturn( userStoreNames );

        final UserStore userStore1 = new UserStore( userStoreName1 );
        userStore1.setConnectorName( "ldap" );
        userStore1.setDefaultStore( true );
        final UserStore userStore2 = new UserStore( userStoreName2 );
        userStore2.setConnectorName( "ad" );
        final UserStores userStores = UserStores.from( userStore1, userStore2 );
        Mockito.when( client.execute( Mockito.isA( GetUserStores.class ) ) ).thenReturn( userStores );

        testSuccess( "userstore_getAll.json" );
    }

}
