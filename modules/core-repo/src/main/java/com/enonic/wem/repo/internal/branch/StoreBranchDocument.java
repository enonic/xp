package com.enonic.wem.repo.internal.branch;

import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeVersionId;

public class StoreBranchDocument
{
    private final Node node;

    private final NodeVersionId nodeVersionId;

    private StoreBranchDocument( final Builder builder )
    {
        this.node = builder.node;
        this.nodeVersionId = builder.nodeVersionId;
    }

    public Node getNode()
    {
        return node;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Node node;

        private NodeVersionId nodeVersionId;

        public Builder node( final Node node )
        {
            this.node = node;
            return this;
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public StoreBranchDocument build()
        {
            return new StoreBranchDocument( this );
        }
    }

}
