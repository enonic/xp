package com.enonic.xp.repo.impl.search.result;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.suggester.Suggestions;

public class SearchResult
{
    private final List<SearchHit> hits;

    private final Aggregations aggregations;

    private final Suggestions suggestions;

    private final long totalHits;

    private final float maxScore;

    private SearchResult( final Builder builder )
    {
        this.hits = builder.searchHits;
        this.aggregations = builder.aggregations;
        this.suggestions = builder.suggestions;
        this.totalHits = builder.totalHits;
        this.maxScore = builder.maxScore;
    }

    public boolean isEmpty()
    {
        return hits.isEmpty();
    }

    public List<SearchHit> getHits()
    {
        return hits;
    }

    public long getTotalHits()
    {
        return totalHits;
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

    public Suggestions getSuggestions()
    {
        return suggestions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<SearchHit> searchHits;

        private Aggregations aggregations = Aggregations.empty();

        private Suggestions suggestions = Suggestions.empty();

        private long totalHits = 0L;

        private float maxScore = 0;

        public Builder hits( final List<SearchHit> searchHits )
        {
            this.searchHits = searchHits;
            return this;
        }

        public Builder aggregations( final Aggregations aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public Builder suggestions( final Suggestions suggestions )
        {
            this.suggestions = suggestions;
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
