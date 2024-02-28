package com.enonic.xp.content;


import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;

public class PatchContentPermissionsResult
{
    private final NodeId nodeId;

    private final Map<Branch, Boolean> branchResult;

    private PatchContentPermissionsResult( final Builder builder )
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

    public Map<Branch, Boolean> getBranchResult()
    {
        return branchResult;
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<Branch, Boolean> branchResult = ImmutableMap.builder();

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public Builder branchResult( final Branch branch, final Boolean result )
        {
            branchResult.put( branch, result );
            return this;
        }

        public PatchContentPermissionsResult build()
        {
            return new PatchContentPermissionsResult( this );
        }
    }

}
