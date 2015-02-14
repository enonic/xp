package com.enonic.wem.repo.internal.index.query;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.core.aggregation.Aggregations;
import com.enonic.xp.core.node.NodeIds;

public final class NodeQueryResult
{
    private final ImmutableSet<NodeQueryResultEntry> entries;

    private final long totalHits;

    private final long hits;

    private final float maxScore;

    private final NodeQueryResultSet nodeQueryResultSet;

    private final Aggregations aggregations;

    private NodeQueryResult( final Builder builder )
    {
        this.entries = ImmutableSet.copyOf( builder.entries );

        this.totalHits = builder.totalHits;

        this.hits = builder.hits;

        this.maxScore = builder.maxScore;

        this.aggregations = builder.aggregations;

        this.nodeQueryResultSet = NodeQueryResultSet.from( builder.entries );
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public long getHits()
    {
        return hits;
    }

    public float getMaxScore()
    {
        return maxScore;
    }

    public Aggregations getAggregations()
    {
        return aggregations;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeIds getNodeIds()
    {
        return nodeQueryResultSet.asNodeIds();
    }

    public NodeQueryResultSet getNodeQueryResultSet()
    {
        return nodeQueryResultSet;
    }

    public static final class Builder
    {
        private final Set<NodeQueryResultEntry> entries = Sets.newLinkedHashSet();

        private long totalHits;

        private long hits;

        private float maxScore;

        private Aggregations aggregations;

        public Builder totalHits( final long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public Builder hits( final long hits )
        {
            this.hits = hits;
            return this;
        }

        public Builder maxScore( final float maxScore )
        {
            this.maxScore = maxScore;
            return this;
        }

        public Builder addEntry( final NodeQueryResultEntry entry )
        {
            this.entries.add( entry );
            return this;
        }

        public Builder aggregations( final Aggregations aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public NodeQueryResult build()
        {
            return new NodeQueryResult( this );
        }
    }

}
