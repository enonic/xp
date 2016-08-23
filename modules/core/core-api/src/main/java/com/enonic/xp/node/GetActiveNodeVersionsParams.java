package com.enonic.xp.node;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.BranchIds;

@Beta
public class GetActiveNodeVersionsParams
{
    private final NodeId nodeId;

    private final BranchIds branchIds;

    private GetActiveNodeVersionsParams( Builder builder )
    {
        nodeId = builder.nodeId;
        branchIds = builder.branchIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public BranchIds getBranchIds()
    {
        return branchIds;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private BranchIds branchIds;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder branches( final BranchIds branchIds )
        {
            this.branchIds = branchIds;
            return this;
        }

        public GetActiveNodeVersionsParams build()
        {
            return new GetActiveNodeVersionsParams( this );
        }
    }
}
