package com.enonic.wem.web.rest.rpc.userstore;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.userstore.GetUserStoreConnectors;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.connector.UserStoreConnectors;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

public class GetUserstoreConnectorsRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final GetUserstoreConnectorsRpcHandler handler = new GetUserstoreConnectorsRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void getConnectors()
        throws Exception
    {
        final UserStoreConnector userStoreConnector = new UserStoreConnector( "local" );
        userStoreConnector.setPluginClass( "LocalPlugin" );
        userStoreConnector.setCreateGroup( true );
        userStoreConnector.setCreateUser( true );
        userStoreConnector.setGroupsStoredRemote( true );
        userStoreConnector.setDeleteGroup( true );
        userStoreConnector.setDeleteUser( true );
        userStoreConnector.setResurrectDeletedGroups( true );
        userStoreConnector.setResurrectDeletedUsers( true );
        userStoreConnector.setReadGroup( true );
        userStoreConnector.setUpdateGroup( true );
        userStoreConnector.setUpdatePassword( true );
        userStoreConnector.setUpdateUser( true );
        final UserStoreConnectors userStoreConnectors = UserStoreConnectors.from(userStoreConnector);
        Mockito.when( client.execute( Mockito.isA( GetUserStoreConnectors.class ) ) ).thenReturn( userStoreConnectors );

        final ObjectNode result = objectNode();
        result.put( "success", true );
        result.put( "total", 1 );
        final ArrayNode jsonConnectors = arrayNode();
        final ObjectNode jsonConnector = objectNode();
        jsonConnector.put( "name", "local" );
        jsonConnector.put( "pluginType", "LocalPlugin" );
        jsonConnector.put( "canCreateUser", true );
        jsonConnector.put( "canUpdateUser", true );
        jsonConnector.put( "canUpdateUserPassword", true );
        jsonConnector.put( "canDeleteUser", true );
        jsonConnector.put( "canCreateGroup", true );
        jsonConnector.put( "canUpdateGroup", true );
        jsonConnector.put( "canDeleteGroup", true );
        jsonConnector.put( "canReadGroup", true );
        jsonConnector.put( "canResurrectDeletedGroups", true );
        jsonConnector.put( "canResurrectDeletedUsers", true );
        jsonConnector.put( "groupsLocal", false );
        jsonConnectors.add( jsonConnector );
        result.put( "userStoreConnectors", jsonConnectors );
        testSuccess( result );
    }

}
