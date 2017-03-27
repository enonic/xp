package com.enonic.xp.node;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.Sets;

@Beta
public class NodeVersionDiffResult
{
    private final NodeIds nodesWithDifferences;

    private final long totalHits;

    private NodeVersionDiffResult( final Builder builder )
    {
        nodesWithDifferences = NodeIds.from( builder.nodeIds );
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

    public static class Builder
    {
        private final Set<NodeId> nodeIds = Sets.newLinkedHashSet();

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
