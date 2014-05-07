package com.enonic.wem.core.index.query;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.aggregation.Aggregations;

public final class QueryResult
{
    protected ImmutableSet<QueryResultEntry> entries;

    protected final long totalHits;

    protected final long hits;

    protected final float maxScore;

    public ImmutableSet<QueryResultEntry> getEntries()
    {
        return entries;
    }

    private final Aggregations aggregations;

    private QueryResult( final Builder builder )
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

    public static Builder newQueryResult()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Set<QueryResultEntry> entries = Sets.newHashSet();

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

        public Builder addEntry( final QueryResultEntry entry )
        {
            this.entries.add( entry );
            return this;
        }

        public Builder aggregations( final Aggregations aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public QueryResult build()
        {
            return new QueryResult( this );
        }
    }

}
