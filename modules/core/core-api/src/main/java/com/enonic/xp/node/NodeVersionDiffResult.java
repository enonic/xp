package com.enonic.xp.node;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.Sets;

@Beta
public class NodeVersionDiffResult
{
    private final NodeIds nodesWithDifferences;

    private NodeVersionDiffResult( final NodeIds nodesWithDifferences )
    {
        this.nodesWithDifferences = nodesWithDifferences;
    }

    public NodeIds getNodesWithDifferences()
    {
        return nodesWithDifferences;
    }

    public static NodeVersionDiffResult empty()
    {
        return new NodeVersionDiffResult( NodeIds.empty() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<NodeId> nodeIds = Sets.newLinkedHashSet();

        public Builder add( final NodeId nodeId )
        {
            this.nodeIds.add( nodeId );
            return this;
        }

        public NodeVersionDiffResult build()
        {
            return new NodeVersionDiffResult( NodeIds.from( nodeIds ) );
        }

    }

}
