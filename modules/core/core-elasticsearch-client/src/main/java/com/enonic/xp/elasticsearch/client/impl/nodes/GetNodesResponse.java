package com.enonic.xp.elasticsearch.client.impl.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.Response;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;

import com.enonic.xp.elasticsearch.client.impl.EsClientResponseResolver;

public class GetNodesResponse
{
    private static final ParseField CLUSTER_NAME_FIELD = new ParseField( "cluster_name" );

    private static final ParseField NODES_FIELD = new ParseField( "nodes" );

    private static final ObjectParser<GetNodesResponse, Void> PARSER =
        new ObjectParser<>( "get_nodes_response", true, GetNodesResponse::new );

    private static final EsClientResponseResolver RESPONSE_RESOLVER = new EsClientResponseResolver<GetNodesResponse>()
    {

        @Override
        public GetNodesResponse doFromXContent( final XContentParser parser )
            throws IOException
        {
            return PARSER.parse( parser, null );
        }

    };

    static
    {
        PARSER.declareString( GetNodesResponse::setClusterName, CLUSTER_NAME_FIELD );
        PARSER.declareObject( GetNodesResponse::setNodes, ( parser, v ) -> {
            final List<Node> nodes = new ArrayList<>();

            XContentParser.Token token;
            while ( !( ( token = parser.nextToken() ) == XContentParser.Token.END_OBJECT &&
                NODES_FIELD.getPreferredName().equals( parser.currentName() ) ) )
            {
                nodes.add( Node.parse( parser, parser.currentName() ) );
            }

            return nodes;
        }, NODES_FIELD );
    }

    private String clusterName;

    private List<Node> nodes;

    public void setClusterName( final String clusterName )
    {
        this.clusterName = clusterName;
    }

    public void setNodes( final List<Node> nodes )
    {
        this.nodes = nodes;
    }

    public String getClusterName()
    {
        return clusterName;
    }

    public List<Node> getNodes()
    {
        return nodes;
    }

    public static GetNodesResponse fromResponse( final Response response )
    {
        return (GetNodesResponse) RESPONSE_RESOLVER.fromResponse( response );
    }

    public static GetNodesResponse fromXContent( final XContentParser parser )
    {
        return (GetNodesResponse) RESPONSE_RESOLVER.fromXContent( parser );
    }

}
