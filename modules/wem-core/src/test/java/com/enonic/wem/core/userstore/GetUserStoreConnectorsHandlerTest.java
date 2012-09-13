package com.enonic.wem.core.userstore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.connector.UserStoreConnectors;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;

import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.connector.config.GroupPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;

public class GetUserStoreConnectorsHandlerTest
{

    private Client client;

    private UserStoreConnectorManager userStoreConnectorManager;

    @Before
    public void setUp()
    {
        userStoreConnectorManager = Mockito.mock( UserStoreConnectorManager.class );

        final GetUserStoreConnectorsHandler handler = new GetUserStoreConnectorsHandler();
        handler.setUserStoreConnectorManager( userStoreConnectorManager );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( handler );
        standardClient.setInvoker( commandInvoker );

        client = standardClient;
    }

    @Test
    public void testGetUserStoreConnectors()
    {
        Map<String, UserStoreConnectorConfig> userStoreConnectorConfigs = new HashMap<>();
        userStoreConnectorConfigs.put( "ad", createUserStoreConnectorConfig( "ad" ) );
        userStoreConnectorConfigs.put( "enonic", createUserStoreConnectorConfig( "enonic" ) );
        UserStoreConnectors expectedResult =
            UserStoreConnectors.from( Arrays.asList( createUserStoreConnector( "ad" ), createUserStoreConnector( "enonic" ) ) );
        Mockito.when( userStoreConnectorManager.getUserStoreConnectorConfigs() ).thenReturn( userStoreConnectorConfigs );
        UserStoreConnectors actualResult = client.execute( Commands.userStore().getConnectors() );

        assert ( expectedResult.getList().containsAll( actualResult.getList() ) );

    }


    private UserStoreConnectorConfig createUserStoreConnectorConfig( String name )
    {
        return new UserStoreConnectorConfig( name, "ldap", UserPolicyConfig.ALL_FALSE, GroupPolicyConfig.ALL_FALSE );
    }

    private UserStoreConnector createUserStoreConnector( String name )
    {
        UserStoreConnector userStoreConnector = new UserStoreConnector( name );
        userStoreConnector.setPluginClass( "ldap" );
        userStoreConnector.setGroupsStoredRemote( true );
        return userStoreConnector;
    }
}
