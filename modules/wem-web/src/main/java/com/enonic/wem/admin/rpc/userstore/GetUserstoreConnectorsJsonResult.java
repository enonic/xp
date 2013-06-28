package com.enonic.wem.admin.rpc.userstore;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.connector.UserStoreConnectors;

class GetUserstoreConnectorsJsonResult
    extends JsonResult
{
    private UserStoreConnectors userStoreConnectors;

    public GetUserstoreConnectorsJsonResult( UserStoreConnectors userStoreConnectors )
    {
        this.userStoreConnectors = userStoreConnectors;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", userStoreConnectors.getSize() );
        json.put( "userStoreConnectors", serialize( userStoreConnectors ) );
    }

    private ArrayNode serialize( final UserStoreConnectors connectors )
    {
        final ArrayNode jsonConnectors = arrayNode();
        for ( UserStoreConnector connector : connectors )
        {
            final ObjectNode jsonConnector = objectNode();
            jsonConnector.put( "name", connector.getName() );
            jsonConnector.put( "pluginType", connector.getPluginClass() );
            jsonConnector.put( "canCreateUser", connector.isCreateUser() );
            jsonConnector.put( "canUpdateUser", connector.isUpdateUser() );
            jsonConnector.put( "canUpdateUserPassword", connector.isUpdatePassword() );
            jsonConnector.put( "canDeleteUser", connector.isDeleteUser() );
            jsonConnector.put( "canCreateGroup", connector.isCreateGroup() );
            jsonConnector.put( "canUpdateGroup", connector.isUpdateGroup() );
            jsonConnector.put( "canDeleteGroup", connector.isDeleteGroup() );
            jsonConnector.put( "canReadGroup", connector.isReadGroup() );
            jsonConnector.put( "canResurrectDeletedGroups", connector.isResurrectDeletedGroups() );
            jsonConnector.put( "canResurrectDeletedUsers", connector.isResurrectDeletedUsers() );
            jsonConnector.put( "groupsLocal", !connector.isGroupsStoredRemote() );
            jsonConnectors.add( jsonConnector );
        }

        return jsonConnectors;
    }
}
