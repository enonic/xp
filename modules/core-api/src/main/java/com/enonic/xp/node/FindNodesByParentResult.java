package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public class FindNodesByParentResult
{
    private final Nodes nodes;

    private final long totalHits;

    private final long hits;

    private FindNodesByParentResult( Builder builder )
    {
        nodes = builder.nodes;
        totalHits = builder.totalHits;
        hits = builder.hits;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Nodes getNodes()
    {
        return nodes;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public long getHits()
    {
        return hits;
    }

    public boolean isEmpty()
    {
        return this.nodes.isEmpty();
    }

    public static final class Builder
    {
        private Nodes nodes;

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder nodes( Nodes nodes )
        {
            this.nodes = nodes;
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

        public FindNodesByParentResult build()
        {
            return new FindNodesByParentResult( this );
        }
    }
}
