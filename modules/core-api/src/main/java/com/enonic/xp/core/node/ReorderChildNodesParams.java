package com.enonic.xp.core.node;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.core.support.AbstractImmutableEntityList;

public class ReorderChildNodesParams
    extends AbstractImmutableEntityList<ReorderChildNodeParams>
{
    private ReorderChildNodesParams( final Builder builder )
    {
        super( ImmutableList.copyOf( builder.orderChildNodeParamsList ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<ReorderChildNodeParams> orderChildNodeParamsList = Lists.newLinkedList();


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
