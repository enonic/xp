package com.enonic.wem.repo.internal.version;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeVersionId;

public class NodeVersionDocument
{
    private final NodeVersionId nodeVersionId;

    private final NodeId nodeId;

    private NodeVersionDocument( final Builder builder )
    {
        this.nodeVersionId = builder.nodeVersionId;
        this.nodeId = builder.nodeId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }


    public static class Builder
    {
        private NodeVersionId nodeVersionId;

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public NodeVersionDocument build()
        {
            validate();
            return new NodeVersionDocument( this );
        }

        private void validate()
        {
            Preconditions.checkNotNull( nodeVersionId );
            Preconditions.checkNotNull( nodeId );
        }

    }


}
