package com.enonic.wem.repo;

public class OrderChildNodeParams
{
    private final NodeId nodeId;

    private final NodeId moveBefore;

    private OrderChildNodeParams( Builder builder )
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

        public OrderChildNodeParams build()
        {
            return new OrderChildNodeParams( this );
        }
    }
}
