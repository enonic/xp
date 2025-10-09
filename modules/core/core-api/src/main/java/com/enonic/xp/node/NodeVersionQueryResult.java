package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class NodeVersionQueryResult
{
    final NodeVersionMetadatas nodeVersionMetadatas;

    private final long totalHits;

    private NodeVersionQueryResult( Builder builder )
    {
        nodeVersionMetadatas = builder.nodeVersionMetadatas;
        totalHits = builder.totalHits;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionMetadatas getNodeVersionMetadatas()
    {
        return nodeVersionMetadatas;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public static final class Builder
    {
        private NodeVersionMetadatas nodeVersionMetadatas;

        private long totalHits;

        private Builder()
        {
        }

        public Builder entityVersions( NodeVersionMetadatas nodeVersionMetadatas )
        {
            this.nodeVersionMetadatas = nodeVersionMetadatas;
            return this;
        }

        public Builder totalHits( long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public NodeVersionQueryResult build()
        {
            return new NodeVersionQueryResult( this );
        }
    }
}
