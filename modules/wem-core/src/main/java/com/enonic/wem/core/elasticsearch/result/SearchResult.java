package com.enonic.wem.core.elasticsearch.result;

import com.enonic.wem.api.aggregation.Aggregations;

public class SearchResult
{
    public SearchResultEntries results;

    public final Aggregations aggregations;

    private SearchResult( final Builder builder )
    {
        this.results = builder.results;
        this.aggregations = builder.aggregations;
    }

    public SearchResultEntries getResults()
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
        public SearchResultEntries results;

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
