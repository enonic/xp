package com.enonic.xp.node;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.suggester.Suggestions;

@PublicApi
public final class FindNodesByMultiRepoQueryResult
{
    private final MultiRepoNodeHits nodeHits;

    private final Aggregations aggregations;

    private final Suggestions suggestions;

    private final long totalHits;

    private final long hits;

    private FindNodesByMultiRepoQueryResult( final Builder builder )
    {
        this.nodeHits = builder.nodeHits.build();
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.aggregations = builder.aggregations;
        this.suggestions = builder.suggestions;
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

    public Suggestions getSuggestions()
    {
        return suggestions;
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

        private Suggestions suggestions;

        private Builder()
        {
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
