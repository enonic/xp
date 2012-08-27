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
    private static final String KEY = "key";

    private static final String NAME = "name";

    private static final String DEFAULT = "default";

    private static final String CONNECTOR = "connector";

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
            ObjectNode userStoreNode = objectNode();
            userStoreNode.put( KEY, entity.getKey().toString() );
            userStoreNode.put( NAME, entity.getName() );
            userStoreNode.put( DEFAULT, entity.isDefaultUserStore() );
            userStoreNode.put( CONNECTOR, entity.getConnectorName() );
            items.add( userStoreNode );
        }
        node.put( "userStores", items );
        return node;
    }
}
