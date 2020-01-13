package com.enonic.xp.elasticsearch.client.impl.cluster.state;

import java.io.IOException;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;

public class IndexShardRoutingTable
{

    private static final ParseField STATE_FIELD = new ParseField( "state" );

    private static final ParseField PRIMARY_FIELD = new ParseField( "primary" );

    private static final ParseField SHARD_FIELD = new ParseField( "shard" );

    private static final ParseField NODE_FIELD = new ParseField( "node" );

    private static final ParseField INDEX_FIELD = new ParseField( "index" );

    private static final ParseField RELOCATING_NODE_FIELD = new ParseField( "relocating_node" );

    private static final ObjectParser<IndexShardRoutingTable, Void> PARSER =
        new ObjectParser<>( "get_cluster_state_response_index_shard_routing_table", true, IndexShardRoutingTable::new );

    static
    {
        PARSER.declareString( IndexShardRoutingTable::setState, STATE_FIELD );
        PARSER.declareBoolean( IndexShardRoutingTable::setPrimary, PRIMARY_FIELD );
        PARSER.declareInt( IndexShardRoutingTable::setShard, SHARD_FIELD );
        PARSER.declareStringOrNull( IndexShardRoutingTable::setNodeId, NODE_FIELD );
        PARSER.declareString( IndexShardRoutingTable::setIndex, INDEX_FIELD );
        PARSER.declareStringOrNull( IndexShardRoutingTable::setRelocatingNodeId, RELOCATING_NODE_FIELD );
    }

    private IndexShardRoutingState state;

    private boolean primary;

    private Integer shard;

    private String nodeId;

    private String relocatingNodeId;

    private String index;

    public static IndexShardRoutingTable parse( final XContentParser parser )
        throws IOException
    {
        return PARSER.parse( parser, null );
    }

    public void setState( final String state )
    {
        this.state = IndexShardRoutingState.valueOf( state );
    }

    public void setPrimary( final boolean primary )
    {
        this.primary = primary;
    }

    public void setShard( final Integer shard )
    {
        this.shard = shard;
    }

    public void setNodeId( final String nodeId )
    {
        this.nodeId = nodeId;
    }

    public void setRelocatingNodeId( final String relocatingNodeId )
    {
        this.relocatingNodeId = relocatingNodeId;
    }

    public void setIndex( final String index )
    {
        this.index = index;
    }

    public IndexShardRoutingState getState()
    {
        return state;
    }

    public boolean isPrimary()
    {
        return primary;
    }

    public Integer getShard()
    {
        return shard;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public String getIndex()
    {
        return index;
    }

    public String getRelocatingNodeId()
    {
        return relocatingNodeId;
    }

}
