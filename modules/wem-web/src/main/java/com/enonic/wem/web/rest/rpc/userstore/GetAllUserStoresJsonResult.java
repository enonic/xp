package com.enonic.wem.web.rest.rpc.userstore;

import java.util.List;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.web.json.JsonResult;

class GetAllUserStoresJsonResult
    extends JsonResult
{
    private UserStores userStores;

    public GetAllUserStoresJsonResult( UserStores userStores )
    {
        this.userStores = userStores;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", userStores.getSize() );
        json.put( "userStores", serialize( userStores.getList() ) );
    }

    private ArrayNode serialize( final List<UserStore> userStores )
    {
        final ArrayNode jsonUserStores = arrayNode();
        for ( UserStore userStore : userStores )
        {
            final ObjectNode jsonUserStore = objectNode();
            jsonUserStore.put( "name", userStore.getName().toString() );
            jsonUserStore.put( "default", userStore.isDefaultStore() );
            jsonUserStore.put( "connector", userStore.getConnectorName() );
            jsonUserStores.add( jsonUserStore );
        }
        
        return jsonUserStores;
    }
}
