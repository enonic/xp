package com.enonic.xp.elasticsearch.client.impl.cluster.state;

import java.io.IOException;

import org.elasticsearch.client.Response;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;

import com.enonic.xp.elasticsearch.client.impl.EsClientResponseResolver;

public class GetClusterStateResponse
{

    private static final ParseField ROUTING_TABLE_FIELD = new ParseField( "routing_table" );

    private static final ParseField MASTER_NODE_FIELD = new ParseField( "master_node" );

    private static final ObjectParser<GetClusterStateResponse, Void> PARSER =
        new ObjectParser<>( "get_cluster_state_response", true, GetClusterStateResponse::new );

    private static final EsClientResponseResolver RESPONSE_RESOLVER = new EsClientResponseResolver<GetClusterStateResponse>()
    {

        @Override
        public GetClusterStateResponse doFromXContent( final XContentParser parser )
            throws IOException
        {
            return PARSER.parse( parser, null );
        }

    };

    static
    {
        PARSER.declareObject( GetClusterStateResponse::routingTable, ( parser, v ) -> RoutingTable.parse( parser ), ROUTING_TABLE_FIELD );
        PARSER.declareString( GetClusterStateResponse::masterNodeId, MASTER_NODE_FIELD );
    }

    private String masterNodeId;

    private RoutingTable routingTable;

    void routingTable( final RoutingTable routingTable )
    {
        this.routingTable = routingTable;
    }

    void masterNodeId( final String masterNodeId )
    {
        this.masterNodeId = masterNodeId;
    }

    public static GetClusterStateResponse fromResponse( final Response response )
    {
        return (GetClusterStateResponse) RESPONSE_RESOLVER.fromResponse( response );
    }

    public static GetClusterStateResponse fromXContent( final XContentParser parser )
    {
        return (GetClusterStateResponse) RESPONSE_RESOLVER.fromXContent( parser );
    }

    public RoutingTable getRoutingTable()
    {
        return routingTable;
    }

    public String getMasterNodeId()
    {
        return masterNodeId;
    }

}
