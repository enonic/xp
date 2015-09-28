package com.enonic.xp.node;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branches;

@Beta
public class GetActiveNodeVersionsParams
{
    private final NodeId nodeId;

    private final Branches branches;

    private GetActiveNodeVersionsParams( Builder builder )
    {
        nodeId = builder.nodeId;
        branches = builder.branches;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private Branches branches;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }

        public GetActiveNodeVersionsParams build()
        {
            return new GetActiveNodeVersionsParams( this );
        }
    }
}
