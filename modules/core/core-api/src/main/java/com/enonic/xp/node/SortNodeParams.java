package com.enonic.xp.node;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.ChildOrder;

@PublicApi
public final class SortNodeParams
{
    private final NodeId nodeId;

    private final ChildOrder childOrder;

    private final ChildOrder manualOrderSeed;

    private final ImmutableList<ReorderChildNodeParams> reorderChildNodes;

    private final NodeDataProcessor processor;

    private final RefreshMode refresh;

    private SortNodeParams( final Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.childOrder = builder.childOrder;
        this.manualOrderSeed = builder.manualOrderSeed;
        this.reorderChildNodes = builder.reorderChildNodes.build();
        this.processor = Objects.requireNonNullElse( builder.processor, ( n, p ) -> n );
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

    public ChildOrder getManualOrderSeed()
    {
        return manualOrderSeed;
    }

    public NodeDataProcessor getProcessor()
    {
        return processor;
    }

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public List<ReorderChildNodeParams> getReorderChildNodes()
    {
        return reorderChildNodes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private ChildOrder childOrder;

        private ChildOrder manualOrderSeed;

        private final ImmutableList.Builder<ReorderChildNodeParams> reorderChildNodes = ImmutableList.builder();

        private NodeDataProcessor processor;

        private RefreshMode refresh;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder manualOrderSeed( final ChildOrder manualOrderSeed )
        {
            this.manualOrderSeed = manualOrderSeed;
            return this;
        }

        public Builder addManualOrder( final ReorderChildNodeParams reorderChildNodeParams )
        {
            this.reorderChildNodes.add( reorderChildNodeParams );
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

        public SortNodeParams build()
        {
            Objects.requireNonNull( nodeId,  "nodeId is required" );
            Objects.requireNonNull( childOrder,  "childOrder is required" );
            return new SortNodeParams( this );
        }
    }
}
