package com.enonic.xp.node;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public class ReorderChildNodesParams
    extends AbstractImmutableEntityList<ReorderChildNodeParams>
{
    private ReorderChildNodesParams( final Builder builder )
    {
        super( builder.orderChildNodeParamsList.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableList.Builder<ReorderChildNodeParams> orderChildNodeParamsList = ImmutableList.builder();

        public Builder add( final ReorderChildNodeParams reorderChildNodeParams )
        {
            this.orderChildNodeParamsList.add( reorderChildNodeParams );
            return this;
        }

        public ReorderChildNodesParams build()
        {
            return new ReorderChildNodesParams( this );
        }

    }


}
