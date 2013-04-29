package com.enonic.wem.web.rest.rpc.space;


import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.Spaces;
import com.enonic.wem.web.json.JsonResult;

final class ListSpacesJsonResult
    extends JsonResult
{
    private Spaces spaces;

    private final SpaceJsonRpcSerializer spaceJsonRpcSerializer;

    ListSpacesJsonResult( final Spaces spaces )
    {
        this.spaces = spaces;
        this.spaceJsonRpcSerializer = new SpaceJsonRpcSerializer();
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", spaces.getSize() );
        json.put( "spaces", serialize( spaces.getList() ) );
    }

    private JsonNode serialize( final List<Space> list )
    {
        final ArrayNode spacesNode = arrayNode();
        for ( Space space : list )
        {
            final ObjectNode spaceNode = spacesNode.addObject();
            spaceJsonRpcSerializer.serialize( space, spaceNode );
            // hasChildren
        }
        return spacesNode;
    }

}
