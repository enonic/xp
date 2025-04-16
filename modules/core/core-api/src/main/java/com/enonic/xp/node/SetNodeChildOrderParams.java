package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.ChildOrder;

@PublicApi
public class SetNodeChildOrderParams
{
    private final NodeId nodeId;

    private final ChildOrder childOrder;

    private final NodeDataProcessor processor;

    private final RefreshMode refresh;

    private SetNodeChildOrderParams( final Builder builder )
    {
        nodeId = builder.nodeId;
        childOrder = builder.childOrder;
        processor = builder.processor;
        this.refresh = builder.refresh;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public NodeDataProcessor getProcessor()
    {
        return processor;
    }

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodeId nodeId;

        private ChildOrder childOrder;

        private NodeDataProcessor processor = ( n, p ) -> n;

        private RefreshMode refresh;

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

        public Builder processor( NodeDataProcessor processor )
        {
            this.processor = processor;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        public SetNodeChildOrderParams build()
        {
            return new SetNodeChildOrderParams( this );
        }
    }
}
