package com.enonic.wem.repo.internal.index.result;

import com.enonic.xp.aggregation.Aggregations;

public class SearchResult
{
    private final SearchHits results;

    private final Aggregations aggregations;

    private SearchResult( final Builder builder )
    {
        this.results = builder.results;
        this.aggregations = builder.aggregations;
    }

    public boolean isEmpty()
    {
        return results.getSize() == 0;
    }

    public SearchHits getResults()
    {
        return results;
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
        private SearchHits results = SearchHits.create().build();

        private Aggregations aggregations;

        public Builder hits( final SearchHits results )
        {
            this.results = results;
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
    }
}
