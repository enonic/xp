package com.enonic.xp.repo.impl.storage;

import java.util.Collection;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;

public final class IndexPushNodeParams
{
    private final Collection<NodeId> nodeIds;

    private final Branch targetBranch;

    private IndexPushNodeParams( Builder builder )
    {
        nodeIds = builder.nodeIds;
        targetBranch = builder.targetBranch;
    }

    public Collection<NodeId> getNodeIds()
    {
        return nodeIds;
    }

    public Branch getTargetBranch()
    {
        return targetBranch;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Collection<NodeId> nodeIds;

        private Branch targetBranch;

        private Builder()
        {
        }

        public Builder nodeIds( final Collection<NodeId> nodeIds )
        {
            this.nodeIds = nodeIds;
            return this;
        }

        public Builder targetBranch( final Branch targetBranch )
        {
            this.targetBranch = targetBranch;
            return this;
        }

        public IndexPushNodeParams build()
        {
            return new IndexPushNodeParams( this );
        }
    }
}
