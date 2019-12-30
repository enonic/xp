package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class RenameNodeParams
{
    private final NodeId nodeId;

    private final NodeName newNodeName;

    private RenameNodeParams( Builder builder )
    {
        nodeId = builder.nodeId;
        newNodeName = builder.newNodeName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeName getNewNodeName()
    {
        return newNodeName;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodeName newNodeName;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodeName( final NodeName nodeName )
        {
            this.newNodeName = nodeName;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.nodeId, "id cannot be null" );
            Preconditions.checkNotNull( this.newNodeName, "name cannot be null" );
        }

        public RenameNodeParams build()
        {
            this.validate();
            return new RenameNodeParams( this );
        }
    }
}
