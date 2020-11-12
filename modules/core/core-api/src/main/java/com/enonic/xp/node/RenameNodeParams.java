package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class RenameNodeParams
{
    private final NodeId nodeId;

    private final NodeName newNodeName;

    private final NodeDataProcessor processor;

    private RenameNodeParams( Builder builder )
    {
        nodeId = builder.nodeId;
        newNodeName = builder.newNodeName;
        processor = builder.processor;
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

    public NodeDataProcessor getProcessor()
    {
        return processor;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodeName newNodeName;

        private NodeDataProcessor processor = ( n ) -> n;

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

        public Builder processor( final NodeDataProcessor processor )
        {
            this.processor = processor;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.nodeId, "id cannot be null" );
            Preconditions.checkNotNull( this.newNodeName, "name cannot be null" );
            Preconditions.checkNotNull( this.processor, "processor cannot be null" );
        }

        public RenameNodeParams build()
        {
            this.validate();
            return new RenameNodeParams( this );
        }
    }
}
