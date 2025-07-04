package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class NodeVersionDiffResult
{
    private final NodeIds nodesWithDifferences;

    private final long totalHits;

    private NodeVersionDiffResult( final Builder builder )
    {
        nodesWithDifferences = builder.nodeIds.build();
        totalHits = builder.totalHits;
    }

    public NodeIds getNodesWithDifferences()
    {
        return nodesWithDifferences;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public static NodeVersionDiffResult empty()
    {
        return new NodeVersionDiffResult( create() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final NodeIds.Builder nodeIds = NodeIds.create();

        private long totalHits;

        private Builder()
        {
        }

        public Builder add( final NodeId nodeId )
        {
            this.nodeIds.add( nodeId );
            return this;
        }

        public NodeVersionDiffResult build()
        {
            return new NodeVersionDiffResult( this );
        }

        public Builder totalHits( final long val )
        {
            totalHits = val;
            return this;
        }
    }

}
