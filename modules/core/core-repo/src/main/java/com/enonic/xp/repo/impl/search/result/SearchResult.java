package com.enonic.xp.repo.impl.search.result;

import java.util.Collection;
import java.util.stream.Collectors;

import com.enonic.xp.aggregation.Aggregations;

public class SearchResult
{
    private final SearchHits hits;

    private final Aggregations aggregations;

    private final long totalHits;

    private final float maxScore;

    private SearchResult( final Builder builder )
    {
        this.hits = builder.searchHits;
        this.aggregations = builder.aggregations;
        this.totalHits = builder.totalHits;
        this.maxScore = builder.maxScore;
    }

    public boolean isEmpty()
    {
        return hits.getSize() == 0;
    }

    public SearchHits getHits()
    {
        return hits;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public long getNumberOfHits()
    {
        return hits.getSize();
    }

    public Collection<String> getIds()
    {
        return this.hits.stream().map( SearchHit::getId ).collect( Collectors.toList() );
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

    public static class Builder
    {
        private SearchHits searchHits;

        private Aggregations aggregations = Aggregations.empty();

        private long totalHits = 0L;

        private float maxScore = 0;

        public Builder hits( final SearchHits searchHits )
        {
            this.searchHits = searchHits;
            return this;
        }

        public Builder aggregations( final Aggregations aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public SearchResult build()
        {
            return new SearchResult( this );
        }

        public Builder totalHits( final long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public Builder maxScore( final float maxScore )
        {
            this.maxScore = maxScore;
            return this;
        }
    }
}
