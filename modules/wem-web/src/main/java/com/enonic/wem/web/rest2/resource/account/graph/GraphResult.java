package com.enonic.wem.web.rest2.resource.account.graph;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;

public final class GraphResult
    extends JsonResult
{
    public static final String KEY_PARAM = "key";

    public static final String TYPE_PARAM = "type";

    public static final String BUILTIN_PARAM = "builtIn";

    public static final String NAME_PARAM = "name";

    public static final String NODETO_PARAM = "nodeTo";

    public static final String DATA_PARAM = "data";

    public static final String ADJACENCIES_PARAM = "adjacencies";

    public static final String ID_PARAM = "id";

    public static final String GRAPH_PARAM = "graph";

    private ObjectNode json;

    public GraphResult()
    {
        json = objectNode();
        json.put( GRAPH_PARAM, arrayNode() );
    }

    @Override
    public JsonNode toJson()
    {
        return json;
    }

    public void addAccountNode( ObjectNode node )
    {
        ArrayNode graph = (ArrayNode) json.get( "graph" );
        graph.add( node );
    }
}
