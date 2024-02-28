package com.enonic.xp.node;


import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.branch.Branch;

public class PatchPermissionsResult
{
    private final NodeId nodeId;

    private final Map<Branch, NodeVersionId> branchResult;

    private PatchPermissionsResult( final Builder builder )
    {
        nodeId = builder.nodeId;
        branchResult = builder.branchResult.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public Map<Branch, NodeVersionId> getBranchResult()
    {
        return branchResult;
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<Branch, NodeVersionId> branchResult = ImmutableMap.builder();

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public Builder branchResult( final Branch branch, final NodeVersionId nodeVersionId )
        {
            branchResult.put( branch, nodeVersionId );
            return this;
        }

        public PatchPermissionsResult build()
        {
            return new PatchPermissionsResult( this );
        }
    }

}
