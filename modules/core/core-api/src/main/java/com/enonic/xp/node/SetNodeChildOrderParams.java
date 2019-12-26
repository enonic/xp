package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.ChildOrder;

@PublicApi
public class SetNodeChildOrderParams
{
    private final NodeId nodeId;

    private final ChildOrder childOrder;

    private SetNodeChildOrderParams( final Builder builder )
    {
        nodeId = builder.nodeId;
        childOrder = builder.childOrder;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodeId nodeId;

        private ChildOrder childOrder;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder childOrder( ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public SetNodeChildOrderParams build()
        {
            return new SetNodeChildOrderParams( this );
        }
    }
}
