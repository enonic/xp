package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeCommitQueryResult
{
    final NodeCommitEntries nodeCommitEntries;

    private final int from;

    private final int size;

    private final long totalHits;

    private final long hits;

    private NodeCommitQueryResult( Builder builder )
    {
        nodeCommitEntries = builder.nodeCommitEntries;
        from = builder.from;
        size = builder.size;
        totalHits = builder.totalHits;
        hits = builder.hits;
    }

    public static NodeCommitQueryResult empty( final long totalHits )
    {
        return create().
            nodeCommitEntries( NodeCommitEntries.empty() ).
            totalHits( totalHits ).
            build();
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

    public long getHits()
    {
        return hits;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public static final class Builder
    {
        private NodeCommitEntries nodeCommitEntries;

        private int from;

        private int size;

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder nodeCommitEntries( NodeCommitEntries nodeCommitEntries )
        {
            this.nodeCommitEntries = nodeCommitEntries;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder to( int to )
        {
            this.size = to;
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

        public NodeCommitQueryResult build()
        {
            return new NodeCommitQueryResult( this );
        }
    }
}
