package com.enonic.xp.elasticsearch.client.impl.cluster.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;

public class IndexRoutingTable
{

    private static final ParseField SHARDS_FIELD = new ParseField( "shards" );

    private static final ObjectParser<IndexRoutingTable, Void> PARSER =
        new ObjectParser<>( "get_cluster_state_response_index_routing_table", true, IndexRoutingTable::new );

    static
    {
        PARSER.declareObject( IndexRoutingTable::setShards, ( parser, v ) -> {
            final List<IndexShardRoutingTable> indexShards = new ArrayList<>();

            XContentParser.Token token;
            while ( !( ( token = parser.nextToken() ) == XContentParser.Token.END_OBJECT &&
                SHARDS_FIELD.getPreferredName().equals( parser.currentName() ) ) )
            {
                if ( token == XContentParser.Token.START_OBJECT )
                {
                    indexShards.add( IndexShardRoutingTable.parse( parser ) );
                }
            }
            return indexShards;
        }, SHARDS_FIELD );
    }

    private String index;

    private List<IndexShardRoutingTable> shards;

    public static IndexRoutingTable parse( final XContentParser parser )
        throws IOException
    {
        return PARSER.parse( parser, null );
    }

    public List<IndexShardRoutingTable> shardsWithState( final IndexShardRoutingState state )
    {
        final List<IndexShardRoutingTable> result = new ArrayList<>();

        for ( IndexShardRoutingTable indexShardRoutingTable : shards )
        {
            if ( indexShardRoutingTable.getState() == state )
            {
                result.add( indexShardRoutingTable );
            }
        }

        return result;
    }

    public void setIndex( final String index )
    {
        this.index = index;
    }

    public void setShards( final List<IndexShardRoutingTable> shards )
    {
        this.shards = shards;
    }

    public String getIndex()
    {
        return index;
    }

    public List<IndexShardRoutingTable> getShards()
    {
        return shards;
    }

}
