package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class FindNodesByParentResult
{
    private final NodeIds nodeIds;

    private final long totalHits;

    private final long hits;

    private FindNodesByParentResult( final long hits, final long totalHits, final NodeIds nodeIds )
    {
        this.hits = hits;
        this.totalHits = totalHits;
        this.nodeIds = nodeIds;
    }

    public static FindNodesByParentResult empty()
    {
        return new FindNodesByParentResult( 0, 0, NodeIds.empty() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeIds getNodeIds()
    {
        return this.nodeIds;
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
        return this.nodeIds.isEmpty();
    }

    public static final class Builder
    {
        private NodeIds nodeIds = NodeIds.empty();

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder nodeIds( NodeIds nodeIds )
        {
            this.nodeIds = nodeIds;
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
            return new FindNodesByParentResult( hits, totalHits, nodeIds );
        }
    }
}
