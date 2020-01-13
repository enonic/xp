package com.enonic.xp.elasticsearch.client.impl.cluster.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;

public class RoutingTable
{

    private static final ParseField INDICES_FIELD = new ParseField( "indices" );

    private static final ObjectParser<RoutingTable, Void> PARSER =
        new ObjectParser<>( "get_cluster_state_response_routing_table", true, RoutingTable::new );

    static
    {
        PARSER.declareObject( RoutingTable::setIndices, ( parser, v ) -> {
            final List<IndexRoutingTable> indices = new ArrayList<>();

            XContentParser.Token token;
            while ( !( ( token = parser.nextToken() ) == XContentParser.Token.END_OBJECT &&
                INDICES_FIELD.getPreferredName().equals( parser.currentName() ) ) )
            {
                indices.add( IndexRoutingTable.parse( parser ) );
            }

            return indices;
        }, INDICES_FIELD );
    }

    private List<IndexRoutingTable> indices;

    public void setIndices( final List<IndexRoutingTable> indices )
    {
        this.indices = indices;
    }

    public List<IndexRoutingTable> getIndices()
    {
        return indices;
    }

    public static RoutingTable parse( XContentParser parser )
        throws IOException
    {
        return PARSER.parse( parser, null );
    }

    public List<IndexShardRoutingTable> shardsWithState( IndexShardRoutingState state )
    {
        final List<IndexShardRoutingTable> shards = new ArrayList<>();

        for ( IndexRoutingTable indexRoutingTable : indices )
        {
            shards.addAll( indexRoutingTable.shardsWithState( state ) );
        }

        return shards;
    }

}
