package com.enonic.wem.core.index.query;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.aggregation.Aggregations;
import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.entity.EntityIds;

public final class NodeQueryResult
{
    private final ImmutableSet<NodeQueryResultEntry> entries;

    private final long totalHits;

    private final long hits;

    private final float maxScore;

    public ImmutableSet<NodeQueryResultEntry> getEntries()
    {
        return entries;
    }

    private final Aggregations aggregations;

    private NodeQueryResult( final Builder builder )
    {
        this.entries = ImmutableSet.copyOf( builder.entries );

        this.totalHits = builder.totalHits;

        this.hits = builder.hits;

        this.maxScore = builder.maxScore;

        this.aggregations = builder.aggregations;
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

    public EntityIds getEntityIds()
    {
        final Set<EntityId> entityIds = Sets.newLinkedHashSet();

        for ( final NodeQueryResultEntry nodeQueryResultEntry : entries )
        {
            entityIds.add( nodeQueryResultEntry.getId() );
        }

        return EntityIds.from( entityIds );
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
