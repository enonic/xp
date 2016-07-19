package com.enonic.xp.node;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.Sets;

import com.enonic.xp.aggregation.Aggregations;

@Beta
public class FindNodesByQueryResult
{
    private final NodeIds nodeIds;

    private final Aggregations aggregations;

    private final long totalHits;

    private final long hits;

    private FindNodesByQueryResult( final Builder builder )
    {
        this.nodeIds = NodeIds.from( builder.nodeIds );
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.aggregations = builder.aggregations;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeIds getNodeIds()
    {
        return this.nodeIds;
    }

    public Aggregations getAggregations()
    {
        return aggregations;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public long getHits()
    {
        return hits;
    }

    public static final class Builder
    {
        private final Set<NodeId> nodeIds = Sets.newLinkedHashSet();

        private long totalHits;

        private long hits;

        private Aggregations aggregations;

        private Builder()
        {
        }

        public Builder aggregations( final Aggregations aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public Builder addNodeId( final NodeId nodeId )
        {
            this.nodeIds.add( nodeId );
            return this;
        }

        public Builder totalHits( long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public Builder hits( long hits )
        {
            this.hits = hits;
            return this;
        }

        public FindNodesByQueryResult build()
        {
            return new FindNodesByQueryResult( this );
        }
    }
}
