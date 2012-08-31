package com.enonic.wem.web.rest2.resource.userstore;

import java.util.Collection;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;

import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;

public class ConnectorResults
    extends JsonResult
{

    private Collection<UserStoreConnectorConfig> connectors;

    public ConnectorResults( Collection<UserStoreConnectorConfig> connectors )
    {
        this.connectors = connectors;
    }

    @Override
    public JsonNode toJson()
    {
        ObjectNode node = objectNode();
        node.put( "total", connectors.size() );
        ArrayNode connectorsNode = arrayNode();
        for ( UserStoreConnectorConfig connector : connectors )
        {
            connectorsNode.add( createConnectorNode( connector ) );
        }
        node.put( "connectors", connectorsNode );
        return node;
    }

    private ObjectNode createConnectorNode( UserStoreConnectorConfig connector )
    {
        ObjectNode connectorNode = objectNode();
        connectorNode.put( "name", connector.getName() );
        connectorNode.put( "canCreateGroup", connector.canCreateGroup() );
        connectorNode.put( "canCreateUser", connector.canCreateUser() );
        connectorNode.put( "canDeleteGroup", connector.canDeleteGroup() );
        connectorNode.put( "canDeleteUser", connector.canDeleteUser() );
        connectorNode.put( "canReadGroup", connector.canReadGroup() );
        connectorNode.put( "canUpdateGroup", connector.canUpdateGroup() );
        connectorNode.put( "canUpdateUser", connector.canUpdateUser() );
        connectorNode.put( "canUpdateUserPassword", connector.canUpdateUserPassword() );
        connectorNode.put( "pluginType", connector.getPluginType() );
        connectorNode.put( "groupsLocal", connector.groupsStoredLocal() );
        return connectorNode;
    }
}
