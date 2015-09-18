package com.enonic.xp.repo.impl.index.result;

import com.enonic.xp.aggregation.Aggregations;

public class SearchResult
{
    private final SearchResultEntries results;

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

    public SearchResultEntries getResults()
    {
        return results;
    }

    public Aggregations getAggregations()
    {
        return aggregations;
    }

    public SearchResultEntry getEntry( final String id )
    {
        return this.results.get( id );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private SearchResultEntries results = SearchResultEntries.create().build();

        private Aggregations aggregations;

        public Builder results( final SearchResultEntries results )
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
