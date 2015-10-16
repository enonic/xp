package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public class NodeVersionQueryResult
{
    final NodeVersionsMetaData nodeVersionsMetaData;

    private final int from;

    private final int size;

    private final long totalHits;

    private final long hits;

    private NodeVersionQueryResult( Builder builder )
    {
        nodeVersionsMetaData = builder.nodeVersionsMetaData;
        from = builder.from;
        size = builder.size;
        totalHits = builder.totalHits;
        hits = builder.hits;
    }

    public static NodeVersionQueryResult empty()
    {
        return create().
            entityVersions( NodeVersionsMetaData.empty() ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionsMetaData getNodeVersionsMetaData()
    {
        return nodeVersionsMetaData;
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
        private NodeVersionsMetaData nodeVersionsMetaData;

        private int from;

        private int size;

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder entityVersions( NodeVersionsMetaData nodeVersionsMetaData )
        {
            this.nodeVersionsMetaData = nodeVersionsMetaData;
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
