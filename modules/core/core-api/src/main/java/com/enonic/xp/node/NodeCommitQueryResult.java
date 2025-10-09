package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class NodeCommitQueryResult
{
    private final NodeCommitEntries nodeCommitEntries;

    private final long totalHits;

    private NodeCommitQueryResult( Builder builder )
    {
        nodeCommitEntries = builder.nodeCommitEntries;
        totalHits = builder.totalHits;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeCommitEntries getNodeCommitEntries()
    {
        return nodeCommitEntries;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public static final class Builder
    {
        private NodeCommitEntries nodeCommitEntries;

        private long totalHits;

        private Builder()
        {
        }

        public Builder nodeCommitEntries( NodeCommitEntries nodeCommitEntries )
        {
            this.nodeCommitEntries = nodeCommitEntries;
            return this;
        }

        public Builder totalHits( long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public NodeCommitQueryResult build()
        {
            return new NodeCommitQueryResult( this );
        }
    }
}
