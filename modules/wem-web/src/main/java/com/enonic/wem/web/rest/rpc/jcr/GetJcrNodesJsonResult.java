package com.enonic.wem.web.rest.rpc.jcr;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

final class GetJcrNodesJsonResult
    extends JsonResult
{
    final ArrayNode nodes;

    public GetJcrNodesJsonResult( final ArrayNode nodes )
    {
        this.nodes = nodes;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "nodes", nodes );
    }

}
