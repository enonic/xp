package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeVersionQueryResult
{
    final NodeVersionsMetadata nodeVersionsMetadata;

    private final int from;

    private final int size;

    private final long totalHits;

    private final long hits;

    private NodeVersionQueryResult( Builder builder )
    {
        nodeVersionsMetadata = builder.nodeVersionsMetadata;
        from = builder.from;
        size = builder.size;
        totalHits = builder.totalHits;
        hits = builder.hits;
    }

    public static NodeVersionQueryResult empty( final long totalHits )
    {
        return create().
            entityVersions( NodeVersionsMetadata.empty() ).
            totalHits( totalHits ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionsMetadata getNodeVersionsMetadata()
    {
        return nodeVersionsMetadata;
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
        private NodeVersionsMetadata nodeVersionsMetadata;

        private int from;

        private int size;

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder entityVersions( NodeVersionsMetadata nodeVersionsMetadata )
        {
            this.nodeVersionsMetadata = nodeVersionsMetadata;
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

        public NodeVersionQueryResult build()
        {
            return new NodeVersionQueryResult( this );
        }
    }
}
