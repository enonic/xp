package com.enonic.xp.core.node;

import com.enonic.xp.core.branch.Branch;

public class SyncWorkResolverParams
{
    private Branch branch;

    private NodeId nodeId;

    private boolean includeChildren;

    private SyncWorkResolverParams( Builder builder )
    {
        branch = builder.branch;
        nodeId = builder.nodeId;
        includeChildren = builder.includeChildren;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Branch branch;

        private NodeId nodeId;

        private boolean includeChildren;

        private Builder()
        {
        }

        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder includeChildren( final boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        public SyncWorkResolverParams build()
        {
            return new SyncWorkResolverParams( this );
        }
    }
}
