package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitId;

public class LoadNodeParams
{
    final Node node;

    final NodeCommitId nodeCommitId;

    private LoadNodeParams( final Builder builder )
    {
        node = builder.node;
        nodeCommitId = builder.nodeCommitId;
    }

    public Node getNode()
    {
        return node;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Node node;

        private NodeCommitId nodeCommitId;

        private Builder()
        {
        }

        public Builder node( final Node node )
        {
            this.node = node;
            return this;
        }

        public Builder nodeCommitId( final NodeCommitId nodeCommitId )
        {
            this.nodeCommitId = nodeCommitId;
            return this;
        }

        public LoadNodeParams build()
        {
            return new LoadNodeParams( this );
        }
    }
}
