package com.enonic.xp.node;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public class ReorderChildNodesParams
    extends AbstractImmutableEntityList<ReorderChildNodeParams>
{
    private NodeDataProcessor processor;

    private ReorderChildNodesParams( final Builder builder )
    {
        super( builder.orderChildNodeParamsList.build() );
        this.processor = builder.processor;
    }

    public NodeDataProcessor getProcessor()
    {
        return processor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableList.Builder<ReorderChildNodeParams> orderChildNodeParamsList = ImmutableList.builder();

        private NodeDataProcessor processor = ( n ) -> n;

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
