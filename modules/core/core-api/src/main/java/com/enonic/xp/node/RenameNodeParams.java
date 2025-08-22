package com.enonic.xp.node;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class RenameNodeParams
{
    private final NodeId nodeId;

    private final NodeName newNodeName;

    private final NodeDataProcessor processor;

    private final RefreshMode refresh;

    private RenameNodeParams( Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.newNodeName = builder.newNodeName;
        this.processor = builder.processor;
        this.refresh = builder.refresh;
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

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodeName newNodeName;

        private NodeDataProcessor processor = ( n, p ) -> n;

        private RefreshMode refresh;

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

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( this.nodeId, "nodeId is required" );
            Objects.requireNonNull( this.newNodeName, "newNodeName is required" );
            Objects.requireNonNull( this.processor, "processor cannot be null" );
        }

        public RenameNodeParams build()
        {
            this.validate();
            return new RenameNodeParams( this );
        }
    }
}
