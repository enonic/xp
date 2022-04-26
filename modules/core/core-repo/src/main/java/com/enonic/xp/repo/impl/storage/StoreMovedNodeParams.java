package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersionId;

public class StoreMovedNodeParams
{
    private final Node node;

    private final NodeVersionId nodeVersionId;

    private StoreMovedNodeParams( Builder builder )
    {
        node = builder.node;
        nodeVersionId = builder.nodeVersionId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node getNode()
    {
        return node;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public static final class Builder
    {
        private Node node;

        private NodeVersionId nodeVersionId;

        private Builder()
        {
        }

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public StoreMovedNodeParams build()
        {
            return new StoreMovedNodeParams( this );
        }
    }
}
