package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.repo.impl.elasticsearch.executor.ExecutorProgressListener;
import com.enonic.xp.repository.RepositoryId;

public final class IndexPushNodeParams
    implements ExecutorProgressListener
{
    private final NodeIds nodeIds;

    private final Branch targetBranch;

    private final RepositoryId targetRepo;

    private final PushNodesListener pushListener;

    private IndexPushNodeParams( Builder builder )
    {
        nodeIds = builder.nodeIds;
        targetBranch = builder.targetBranch;
        targetRepo = builder.targetRepo;
        pushListener = builder.pushListener;
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

    public PushNodesListener getPushListener()
    {
        return pushListener;
    }

    @Override
    public void progress( final int count )
    {
        if ( pushListener != null )
        {
            pushListener.nodesPushed( count );
        }
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

        private PushNodesListener pushListener;

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

        public Builder pushListener( final PushNodesListener pushListener )
        {
            this.pushListener = pushListener;
            return this;
        }

        public IndexPushNodeParams build()
        {
            return new IndexPushNodeParams( this );
        }
    }
}
