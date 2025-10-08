package com.enonic.xp.node;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class FindNodesByParentResult
{
    private final NodeIds nodeIds;

    private final long totalHits;

    private FindNodesByParentResult( final long totalHits, final NodeIds nodeIds )
    {
        this.totalHits = totalHits;
        this.nodeIds = Objects.requireNonNull( nodeIds );
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

    public static final class Builder
    {
        private NodeIds nodeIds;

        private long totalHits;

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

        public FindNodesByParentResult build()
        {
            return new FindNodesByParentResult( totalHits, nodeIds );
        }
    }
}
