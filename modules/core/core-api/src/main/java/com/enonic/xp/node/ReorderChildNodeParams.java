package com.enonic.xp.node;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ReorderChildNodeParams
{
    private final NodeId nodeId;

    private final NodeId moveBefore;

    private ReorderChildNodeParams( Builder builder )
    {
        nodeId = builder.nodeId;
        moveBefore = builder.moveBefore;
    }


    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeId getMoveBefore()
    {
        return moveBefore;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodeId nodeId;

        private NodeId moveBefore;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder moveBefore( NodeId moveBefore )
        {
            this.moveBefore = moveBefore;
            return this;
        }

        public ReorderChildNodeParams build()
        {
            Objects.requireNonNull( nodeId, "nodeId is required" );
            Preconditions.checkArgument( !nodeId.equals( moveBefore ), "nodeId and moveBefore must be different" );
            return new ReorderChildNodeParams( this );
        }
    }
}
