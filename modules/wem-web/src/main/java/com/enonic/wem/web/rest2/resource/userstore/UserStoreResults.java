package com.enonic.wem.web.rest2.resource.userstore;

import java.util.Collection;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;

import com.enonic.cms.core.security.userstore.UserStoreEntity;

public class UserStoreResults
    extends JsonResult
{

    private final Collection<UserStoreEntity> userStores;

    public UserStoreResults( final Collection<UserStoreEntity> userStores )
    {
        this.userStores = userStores;
    }

    @Override
    public JsonNode toJson()
    {
        ObjectNode node = objectNode();
        node.put( "total", userStores.size() );
        ArrayNode items = arrayNode();
        for ( UserStoreEntity entity : userStores )
        {
            items.add( createUserStoreNode( entity ) );
        }
        node.put( "userStores", items );
        return node;
    }

    private ObjectNode createUserStoreNode( UserStoreEntity entity )
    {
        ObjectNode userStoreNode = objectNode();
        userStoreNode.put( "key", entity.getKey().toString() );
        userStoreNode.put( "name", entity.getName() );
        userStoreNode.put( "default", entity.isDefaultUserStore() );
        userStoreNode.put( "connector", entity.getConnectorName() );
        return userStoreNode;
    }
}
