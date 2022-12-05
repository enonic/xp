package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.repository.RepositoryId;

public final class IndexPushNodeParams
{
    private final NodeIds nodeIds;

    private final Branch targetBranch;

    private final RepositoryId targetRepo;

    private IndexPushNodeParams( Builder builder )
    {
        nodeIds = builder.nodeIds;
        targetBranch = builder.targetBranch;
        targetRepo = builder.targetRepo;
    }

    public NodeIds getNodeIds()
    {
        return nodeIds;
    }

    public Branch getTargetBranch()
    {
        return targetBranch;
    }

    public RepositoryId getTargetRepo()
    {
        return targetRepo;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeIds nodeIds;

        private Branch targetBranch;

        private RepositoryId targetRepo;

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

        public Builder targetRepo( final RepositoryId targetRepo )
        {
            this.targetRepo = targetRepo;
            return this;
        }

        public IndexPushNodeParams build()
        {
            return new IndexPushNodeParams( this );
        }
    }
}
