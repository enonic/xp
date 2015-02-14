package com.enonic.xp.content;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntityList;

public class ReorderChildContentsParams
    extends AbstractImmutableEntityList<ReorderChildParams>
{
    private ReorderChildContentsParams( final Builder builder )
    {
        super( ImmutableList.copyOf( builder.orderChildContentParamsList ) );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static class Builder
    {
        private final List<ReorderChildParams> orderChildContentParamsList = Lists.newLinkedList();


        public Builder add( final ReorderChildParams orderChildNodeParams )
        {
            this.orderChildContentParamsList.add( orderChildNodeParams );
            return this;
        }

        public ReorderChildContentsParams build()
        {
            return new ReorderChildContentsParams( this );
        }

    }

}
