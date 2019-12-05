package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersionId;

public class MoveNodeParams
{
    private final Node node;

    private final boolean updateMetadataOnly;

    private final NodeVersionId nodeVersionId;

    private MoveNodeParams( Builder builder )
    {
        node = builder.node;
        updateMetadataOnly = builder.updateMetadataOnly;
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


    public boolean isUpdateMetadataOnly()
    {
        return updateMetadataOnly;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public static final class Builder
    {
        private Node node;

        private boolean updateMetadataOnly = false;

        private NodeVersionId nodeVersionId;

        private Builder()
        {
        }

        public Builder node( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder updateMetadataOnly( boolean updateMetadataOnly )
        {
            this.updateMetadataOnly = updateMetadataOnly;
            return this;
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public MoveNodeParams build()
        {
            return new MoveNodeParams( this );
        }
    }
}
