package com.enonic.wem.core.userstore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.userstore.GetUserStoreConnectors;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.connector.UserStoreConnectors;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.connector.config.GroupPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;

public class GetUserStoreConnectorsHandlerTest
    extends AbstractCommandHandlerTest
{
    private UserStoreConnectorManager userStoreConnectorManager;

    private GetUserStoreConnectorsHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        userStoreConnectorManager = Mockito.mock( UserStoreConnectorManager.class );

        handler = new GetUserStoreConnectorsHandler();
        handler.setUserStoreConnectorManager( userStoreConnectorManager );
    }

    @Test
    public void testGetUserStoreConnectors()
        throws Exception
    {
        Map<String, UserStoreConnectorConfig> userStoreConnectorConfigs = new HashMap<String, UserStoreConnectorConfig>();
        userStoreConnectorConfigs.put( "ad", createUserStoreConnectorConfig( "ad" ) );
        userStoreConnectorConfigs.put( "enonic", createUserStoreConnectorConfig( "enonic" ) );
        UserStoreConnectors expectedResult =
            UserStoreConnectors.from( Arrays.asList( createUserStoreConnector( "ad" ), createUserStoreConnector( "enonic" ) ) );
        Mockito.when( userStoreConnectorManager.getUserStoreConnectorConfigs() ).thenReturn( userStoreConnectorConfigs );
        final GetUserStoreConnectors command = Commands.userStore().getConnectors();
        this.handler.handle( this.context, command );
        UserStoreConnectors actualResult = command.getResult();

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
