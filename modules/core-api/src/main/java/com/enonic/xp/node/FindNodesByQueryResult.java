package com.enonic.xp.node;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.aggregation.Aggregations;

public class FindNodesByQueryResult
{
    private final Nodes nodes;

    private final Aggregations aggregations;

    private final long totalHits;

    private final long hits;

    private FindNodesByQueryResult( final Builder builder )
    {
        this.nodes = Nodes.from( builder.nodes );
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.aggregations = builder.aggregations;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Nodes getNodes()
    {
        return this.nodes;
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
        private final Set<Node> nodes = Sets.newLinkedHashSet();

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

        public Builder addNode( final Node node )
        {
            this.nodes.add( node );
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
