package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeIds;

public final class IndexPushNodeParams
{
    private final NodeIds nodeIds;

    private final Branch targetBranch;

    private IndexPushNodeParams( Builder builder )
    {
        nodeIds = builder.nodeIds;
        targetBranch = builder.targetBranch;
    }

    public NodeIds getNodeIds()
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
        private NodeIds nodeIds;

        private Branch targetBranch;

        private Builder()
        {
        }

        public Builder nodeIds( final NodeIds nodeIds )
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
