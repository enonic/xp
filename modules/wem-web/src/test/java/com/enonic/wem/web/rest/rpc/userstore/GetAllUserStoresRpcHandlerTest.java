package com.enonic.wem.web.rest.rpc.userstore;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.userstore.FindAllUserStores;
import com.enonic.wem.api.command.userstore.GetUserStores;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.api.userstore.statistics.UserStoreStatistics;
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
        userStore1.setAdministrators( AccountKeys.from( "user:enonic:admin1" ) );
        final UserStoreStatistics statistics = new UserStoreStatistics();
        statistics.setNumGroups( 33 );
        statistics.setNumRoles( 5 );
        statistics.setNumUsers( 57 );
        userStore1.setStatistics( statistics );
        final UserStoreConfig config = new UserStoreConfig();
        final UserStoreFieldConfig phoneField = new UserStoreFieldConfig( "phone" );
        phoneField.setReadOnly( true );
        final UserStoreFieldConfig organizationField = new UserStoreFieldConfig( "organization" );
        organizationField.setRequired( true );
        final UserStoreFieldConfig firstNameField = new UserStoreFieldConfig( "firstName" );
        firstNameField.setRemote( true );
        config.addField( phoneField );
        config.addField( organizationField );
        config.addField( firstNameField );
        userStore1.setConfig( config );

        final UserStore userStore2 = new UserStore( userStoreName2 );
        userStore2.setConnectorName( "ad" );
        userStore2.setAdministrators( AccountKeys.empty() );
        userStore2.setStatistics( new UserStoreStatistics() );
        userStore2.setConfig( new UserStoreConfig() );

        final UserStores userStores = UserStores.from( userStore1, userStore2 );
        Mockito.when( client.execute( Mockito.isA( GetUserStores.class ) ) ).thenReturn( userStores );

        testSuccess( "userstore_getAll.json" );
    }

}
