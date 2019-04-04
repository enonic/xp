package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeHits;
import com.enonic.xp.script.serializer.MapGenerator;

public final class NodeQueryResultMapper
    extends AbstractQueryResultMapper
{
    private final NodeHits nodeHits;

    private final long total;

    private final Aggregations aggregations;

    public NodeQueryResultMapper( final FindNodesByQueryResult result )
    {
        this.nodeHits = result.getNodeHits();
        this.total = result.getTotalHits();
        this.aggregations = result.getAggregations();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", this.total );
        gen.value( "count", this.nodeHits.getSize() );
        serialize( gen, this.nodeHits );
        serialize( gen, aggregations );
    }

    private void serialize( final MapGenerator gen, final NodeHits nodeHits )
    {
        gen.array( "hits" );
        for ( NodeHit nodeHit : nodeHits )
        {
            gen.map();
            gen.value( "id", nodeHit.getNodeId() );
            gen.value( "score", Float.isNaN( nodeHit.getScore() ) ? 0.0 : nodeHit.getScore() );
            serialize( gen, nodeHit.getExplanation() );
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
