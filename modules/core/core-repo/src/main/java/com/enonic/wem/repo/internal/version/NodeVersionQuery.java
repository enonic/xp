package com.enonic.wem.repo.internal.version;

import com.enonic.xp.node.NodeId;

public class NodeVersionQuery
{
    private final Integer from;

    private final Integer size;

    private final NodeId nodeId;

    private NodeVersionQuery( Builder builder )
    {
        from = builder.from;
        size = builder.size;
        nodeId = builder.nodeId;
    }

    public Integer getFrom()
    {
        return from;
    }

    public Integer getSize()
    {
        return size;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Integer from = 0;

        private Integer size = 10;

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder from( Integer from )
        {
            this.from = from;
            return this;
        }

        public Builder size( Integer size )
        {
            this.size = size;
            return this;
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public NodeVersionQuery build()
        {
            return new NodeVersionQuery( this );
        }
    }
}
