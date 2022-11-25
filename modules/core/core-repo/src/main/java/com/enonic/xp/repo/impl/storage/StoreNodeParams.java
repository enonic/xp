package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitId;

public class StoreNodeParams
{
    final Node node;

    final NodeCommitId nodeCommitId;

    final boolean overrideVersion;

    private StoreNodeParams( final Builder builder )
    {
        node = builder.node;
        nodeCommitId = builder.nodeCommitId;
        overrideVersion = builder.overrideVersion;
    }

    public Node getNode()
    {
        return node;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public boolean isOverrideVersion()
    {
        return overrideVersion;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Node node;

        private NodeCommitId nodeCommitId;

        private boolean overrideVersion;

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

        public Builder overrideVersion() {
            this.overrideVersion = true;
            return this;
        }

        public StoreNodeParams build()
        {
            return new StoreNodeParams( this );
        }
    }
}
