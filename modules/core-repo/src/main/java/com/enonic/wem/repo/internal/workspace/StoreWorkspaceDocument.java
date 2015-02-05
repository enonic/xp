package com.enonic.wem.repo.internal.workspace;

import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeVersionId;

public class StoreWorkspaceDocument
{
    private final Node node;

    private final NodeVersionId nodeVersionId;

    private StoreWorkspaceDocument( final Builder builder )
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

        public StoreWorkspaceDocument build()
        {
            return new StoreWorkspaceDocument( this );
        }
    }

}
