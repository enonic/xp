package com.enonic.wem.admin.rpc.userstore;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.userstore.FindAllUserStores;
import com.enonic.wem.api.command.userstore.GetUserStores;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;

public class GetAllUserStoresRpcHandlerTest
    extends AbstractUserStoreRpcHandlerTest

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

        final UserStore userStore1 = createUserStore( userStoreName1, "ldap", true, true, true, true );
        final UserStore userStore2 = createUserStore( userStoreName2, "ad", false, false, true, false );

        final UserStores userStores = UserStores.from( userStore1, userStore2 );
        Mockito.when( client.execute( Mockito.isA( GetUserStores.class ) ) ).thenReturn( userStores );

        testSuccess( "userstore_getAll.json" );
    }

}
