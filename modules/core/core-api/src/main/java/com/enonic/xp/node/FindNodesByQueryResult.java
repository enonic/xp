package com.enonic.xp.node;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.suggester.Suggestions;

@PublicApi
public final class FindNodesByQueryResult
{
    private final NodeHits nodeHits;

    private final Aggregations aggregations;

    private final Suggestions suggestions;

    private final long totalHits;

    private FindNodesByQueryResult( final Builder builder )
    {
        this.nodeHits = builder.nodeHits.build();
        this.totalHits = builder.totalHits;
        this.aggregations = builder.aggregations;
        this.suggestions = builder.suggestions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeIds getNodeIds()
    {
        return this.nodeHits.getNodeIds();
    }

    public NodeHits getNodeHits()
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

    public static final class Builder
    {
        private final NodeHits.Builder nodeHits = NodeHits.create();

        private long totalHits;

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


        public Builder addNodeHit( final NodeHit nodeHit )
        {
            this.nodeHits.add( nodeHit );
            return this;
        }

        public Builder totalHits( long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public FindNodesByQueryResult build()
        {
            return new FindNodesByQueryResult( this );
        }
    }
}
