package com.enonic.wem.api.node;

import java.util.Set;

import com.google.common.collect.Sets;

public class WorkspaceDiffResult
{
    private final NodeIds nodesWithDifferences;

    private WorkspaceDiffResult( final NodeIds nodesWithDifferences )
    {
        this.nodesWithDifferences = nodesWithDifferences;
    }

    public NodeIds getNodesWithDifferences()
    {
        return nodesWithDifferences;
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

        public WorkspaceDiffResult build()
        {
            return new WorkspaceDiffResult( NodeIds.from( nodeIds ) );
        }

    }

}
