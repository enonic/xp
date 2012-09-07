package com.enonic.wem.web.data.account;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.web.data.binary.AccountImageUriResolver;
import com.enonic.wem.web.json.result.JsonResult;

final class GetAccountGraphJsonResult
    extends JsonResult
{
    private final Map<Account, List<Account>> graphData;

    // Nodes ids should be unique, even among different graphs
    private final String timestamp;

    public GetAccountGraphJsonResult( boolean success, Map<Account, List<Account>> graphData )
    {
        super( success );
        this.graphData = graphData;
        this.timestamp = String.valueOf( System.currentTimeMillis() );
    }


    @Override
    protected void serialize( final ObjectNode json )
    {
        ArrayNode graph = arrayNode();
        for ( Map.Entry<Account, List<Account>> entry : graphData.entrySet() )
        {
            graph.add( createGraphNode( entry.getKey(), entry.getValue() ) );
        }
        json.put( "graph", graph );
    }

    private String getGraphNodeId( Account account )
    {
        StringBuilder id = new StringBuilder( timestamp );
        id.append( "_" );
        id.append( account.getKey() );
        return id.toString();
    }

    private ObjectNode createGraphNodeData( Account account )
    {
        AccountKey accountKey = account.getKey();
        ObjectNode node = objectNode();
        node.put( "type", accountKey.getType().toString().toLowerCase() );
        node.put( "key", accountKey.toString() );
        node.put( "image_uri", AccountImageUriResolver.resolve( account ) );
        node.put( "name", accountKey.getLocalName() );
        return node;
    }

    private ObjectNode createGraphNode( Account account, List<Account> adjacencies )
    {
        ObjectNode node = objectNode();
        node.put( "id", getGraphNodeId( account ) );
        node.put( "name", account.getDisplayName() );
        node.put( "data", createGraphNodeData( account ) );
        ArrayNode adjacenciesNode = arrayNode();
        if ( adjacencies != null )
        {
            for ( Account adjacency : adjacencies )
            {
                ObjectNode nodeTo = objectNode();
                nodeTo.put( "nodeTo", getGraphNodeId( adjacency ) );
                adjacenciesNode.add( nodeTo );
            }
        }
        node.put( "adjacencies", adjacenciesNode );
        return node;
    }
}
