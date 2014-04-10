package com.enonic.wem.core.index.entity;

import java.util.Set;

import org.elasticsearch.search.SearchHit;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.aggregation.Aggregations;

public class EntityQueryResult
{
    private final ImmutableSet<EntityQueryResultEntry> entries;

    private final long totalHits;

    private final long hits;

    private final float maxScore;

    private final Aggregations aggregations;

    private EntityQueryResult( final Builder builder )
    {
        this.entries = ImmutableSet.copyOf( builder.entries );
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.maxScore = builder.maxScore;
        this.aggregations = builder.aggregations;
    }

    public static Builder newResult()
    {
        return new Builder();
    }

    public ImmutableSet<EntityQueryResultEntry> getEntries()
    {
        return entries;
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

    public static class Builder
    {
        private Set<EntityQueryResultEntry> entries = Sets.newLinkedHashSet();

        private long totalHits;

        private long hits;

        private float maxScore;

        private Aggregations aggregations;

        public EntityQueryResult build()
        {
            return new EntityQueryResult( this );
        }

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

        public Builder addEntries( final SearchHit[] hits )
        {
            for ( final SearchHit hit : hits )
            {
                entries.add( new EntityQueryResultEntry( hit.score(), hit.id() ) );
            }

            return this;
        }

        public Builder aggregations( final Aggregations aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }
    }

}
