package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class NodeResultMapper
    implements MapSerializable
{
    private final NodeIds nodeIds;

    private final long total;

    private final Aggregations aggregations;

    public NodeResultMapper( final FindNodesByQueryResult result )
    {
        this.nodeIds = result.getNodeIds();
        this.total = result.getTotalHits();
        this.aggregations = result.getAggregations();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", this.total );
        gen.value( "count", this.nodeIds.getSize() );
        serialize( gen, this.nodeIds );
        serialize( gen, aggregations );
    }

    private void serialize( final MapGenerator gen, final NodeIds nodeIds )
    {
        gen.array( "hits" );
        for ( NodeId nodeId : nodeIds )
        {
            gen.map();
            gen.value( "id", nodeId.toString() );
            gen.end();
        }
        gen.end();
    }

    private void serialize( final MapGenerator gen, final Aggregations aggregations )
    {
        if ( aggregations != null )
        {
            gen.map( "aggregations" );
            new AggregationMapper( aggregations ).serialize( gen );
            gen.end();
        }
    }
}
