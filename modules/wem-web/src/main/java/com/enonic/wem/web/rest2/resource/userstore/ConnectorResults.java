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

    public static final String CONNECTORS = "connectors";

    public static final String NAME = "name";

    public static final String CAN_CREATE_GROUP = "canCreateGroup";

    public static final String CAN_CREATE_USER = "canCreateUser";

    public static final String CAN_DELETE_GROUP = "canDeleteGroup";

    public static final String CAN_DELETE_USER = "canDeleteUser";

    public static final String CAN_READ_GROUP = "canReadGroup";

    public static final String CAN_UPDATE_GROUP = "canUpdateGroup";

    public static final String CAN_UPDATE_USER = "canUpdateUser";

    public static final String CAN_UPDATE_USER_PASSWORD = "canUpdateUserPassword";

    public static final String PLUGIN_TYPE = "pluginType";

    public static final String GROUPS_LOCAL = "groupsLocal";

    private Collection<UserStoreConnectorConfig> connectors;

    public ConnectorResults( Collection<UserStoreConnectorConfig> connectors )
    {
        this.connectors = connectors;
    }

    @Override
    public JsonNode toJson()
    {
        ObjectNode node = objectNode();
        node.put( TOTAL, connectors.size() );
        ArrayNode connectorsNode = arrayNode();
        for ( UserStoreConnectorConfig connector : connectors )
        {
            connectorsNode.add( createConnectorNode( connector ) );
        }
        node.put( CONNECTORS, connectorsNode );
        return node;
    }

    private ObjectNode createConnectorNode( UserStoreConnectorConfig connector )
    {
        ObjectNode connectorNode = objectNode();
        connectorNode.put( NAME, connector.getName() );
        connectorNode.put( CAN_CREATE_GROUP, connector.canCreateGroup() );
        connectorNode.put( CAN_CREATE_USER, connector.canCreateUser() );
        connectorNode.put( CAN_DELETE_GROUP, connector.canDeleteGroup() );
        connectorNode.put( CAN_DELETE_USER, connector.canDeleteUser() );
        connectorNode.put( CAN_READ_GROUP, connector.canReadGroup() );
        connectorNode.put( CAN_UPDATE_GROUP, connector.canUpdateGroup() );
        connectorNode.put( CAN_UPDATE_USER, connector.canUpdateUser() );
        connectorNode.put( CAN_UPDATE_USER_PASSWORD, connector.canUpdateUserPassword() );
        connectorNode.put( PLUGIN_TYPE, connector.getPluginType() );
        connectorNode.put( GROUPS_LOCAL, connector.groupsStoredLocal() );
        return connectorNode;
    }
}
