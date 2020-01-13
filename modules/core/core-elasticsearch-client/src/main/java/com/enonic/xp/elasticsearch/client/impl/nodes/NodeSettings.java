package com.enonic.xp.elasticsearch.client.impl.nodes;

import java.io.IOException;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;

public class NodeSettings
{

    private static final ParseField NETWORK_FILED = new ParseField( "network" );

    private static final ObjectParser<NodeSettings, Void> PARSER =
        new ObjectParser<>( "get_nodes_response_node_settings", true, NodeSettings::new );

    static
    {
        PARSER.declareObject( NodeSettings::setNetwork, ( parser, v ) -> Network.parse( parser ), NETWORK_FILED );
    }

    private Network network;

    public static NodeSettings parse( final XContentParser parser )
        throws IOException
    {
        return PARSER.parse( parser, null );
    }

    public Network getNetwork()
    {
        return network;
    }

    public void setNetwork( final Network network )
    {
        this.network = network;
    }

    public static class Network
    {

        private static final ParseField HOST_FILED = new ParseField( "host" );

        private static final ObjectParser<Network, Void> PARSER =
            new ObjectParser<>( "get_nodes_response_node_settings_network", true, Network::new );

        static
        {
            PARSER.declareStringOrNull( Network::setHost, HOST_FILED );
        }

        private String host;

        public static Network parse( final XContentParser parser )
            throws IOException
        {
            return PARSER.parse( parser, null );
        }

        public String getHost()
        {
            return host;
        }

        public void setHost( final String host )
        {
            this.host = host;
        }

    }

}
