package com.enonic.xp.node;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class FindNodesByMultiRepoQueryResult
{
    private final MultiRepoNodeHits nodeHits;

    private final Aggregations aggregations;

    private final long totalHits;

    private final long hits;

    private FindNodesByMultiRepoQueryResult( final Builder builder )
    {
        this.nodeHits = builder.nodeHits.build();
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.aggregations = builder.aggregations;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public MultiRepoNodeHits getNodeHits()
    {
        return nodeHits;
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
        private final MultiRepoNodeHits.Builder nodeHits = MultiRepoNodeHits.create();

        private long totalHits;

        private long hits;

        private Aggregations aggregations;

        private Builder()
        {
        }

        public Builder aggregations( final Aggregations aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public Builder addNodeHit( final MultiRepoNodeHit nodeHit )
        {
            this.nodeHits.add( nodeHit );
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

        public FindNodesByMultiRepoQueryResult build()
        {
            return new FindNodesByMultiRepoQueryResult( this );
        }
    }
}
