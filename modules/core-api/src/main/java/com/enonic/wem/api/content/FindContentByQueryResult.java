package com.enonic.wem.api.content;

import com.enonic.wem.api.aggregation.Aggregations;

public final class FindContentByQueryResult
{
    private Contents contents;

    private final Aggregations aggregations;

    private long totalHits;

    private long hits;

    private FindContentByQueryResult( final Builder builder )
    {
        this.contents = builder.contents;
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.aggregations = builder.aggregations;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Contents getContents()
    {
        return contents;
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
        private Contents contents;

        private Aggregations aggregations;

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder aggregations( final Aggregations aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public Builder contents( Contents contents )
        {
            this.contents = contents;
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

        public FindContentByQueryResult build()
        {
            return new FindContentByQueryResult( this );
        }
    }
}
