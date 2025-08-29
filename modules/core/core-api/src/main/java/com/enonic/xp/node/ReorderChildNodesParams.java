package com.enonic.xp.node;

import java.util.Iterator;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ReorderChildNodesParams
    implements Iterable<ReorderChildNodeParams>
{
    private final NodeDataProcessor processor;

    private final RefreshMode refresh;

    private final ImmutableList<ReorderChildNodeParams> list;

    private ReorderChildNodesParams( final Builder builder )
    {
        this.list = builder.orderChildNodeParamsList.build();
        this.processor = builder.processor;
        this.refresh = builder.refresh;
    }

    public NodeDataProcessor getProcessor()
    {
        return processor;
    }

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public Iterator<ReorderChildNodeParams> iterator()
    {
        return list.iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ReorderChildNodeParams> orderChildNodeParamsList = ImmutableList.builder();

        private NodeDataProcessor processor = ( n, p ) -> n;

        private RefreshMode refresh;

        private Builder()
        {
        }

        public Builder add( final ReorderChildNodeParams reorderChildNodeParams )
        {
            this.orderChildNodeParamsList.add( reorderChildNodeParams );
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
            Objects.requireNonNull( this.processor, "processor is required" );
        }

        public ReorderChildNodesParams build()
        {
            validate();
            return new ReorderChildNodesParams( this );
        }

    }


}
