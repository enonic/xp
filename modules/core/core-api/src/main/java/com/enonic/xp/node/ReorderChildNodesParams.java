package com.enonic.xp.node;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ReorderChildNodesParams
    extends AbstractImmutableEntityList<ReorderChildNodeParams>
{
    private final NodeDataProcessor processor;

    private final RefreshMode refresh;

    private ReorderChildNodesParams( final Builder builder )
    {
        super( builder.orderChildNodeParamsList.build() );
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
            Preconditions.checkNotNull( this.processor, "processor must be set" );
        }

        public ReorderChildNodesParams build()
        {
            validate();
            return new ReorderChildNodesParams( this );
        }

    }


}
