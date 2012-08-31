package com.enonic.wem.web.rest2.resource.account.graph;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;

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

    public static final String IMAGE_URI_PARAM = "image_uri";


    private ArrayNode graph;

    public GraphResult()
    {
        graph = arrayNode();
    }

    @Override
    public JsonNode toJson()
    {
        ObjectNode json = objectNode();
        json.put( GRAPH_PARAM, graph );
        return json;
    }

    public void addAccountNode( ObjectNode node )
    {
        graph.add( node );
    }

    public void merge( GraphResult graphResult )
    {
        ArrayNode graphToMerge = graphResult.getGraph();
        graph.addAll( graphToMerge );
    }

    protected ArrayNode getGraph()
    {
        return graph;
    }

    private boolean containsKey( String prefix, String key )
    {
        List<String> keysInGraph = getGraph().findValuesAsText( ID_PARAM );
        String nodeKey = new StringBuffer( prefix ).append( "_" ).append( key ).toString();
        return keysInGraph.contains( nodeKey );
    }

    public boolean containsEntity( String prefix, GroupEntity group )
    {
        if ( group.isOfType( GroupType.USER, false ) )
        {
            return containsEntity( prefix, group.getUser() );
        }
        return containsKey( prefix, group.getGroupKey().toString() );
    }

    public boolean containsEntity( String prefix, UserEntity user )
    {
        return containsKey( prefix, user.getKey().toString() );
    }
}
