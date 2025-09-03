package com.enonic.xp.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.ChildOrder;

@PublicApi
public final class SortContentParams
{
    private final ContentId contentId;

    private final ChildOrder childOrder;

    private final ChildOrder manualOrderSeed;

    private final ImmutableList<ReorderChildContentParams> reorderChildContents;

    private final boolean stopInherit;

    private SortContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.childOrder = builder.childOrder;
        this.manualOrderSeed = builder.manualOrderSeed;
        this.reorderChildContents = builder.reorderChildContents.build();
        stopInherit = builder.stopInherit;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public ChildOrder getManualOrderSeed()
    {
        return manualOrderSeed;
    }

    public List<ReorderChildContentParams> getReorderChildContents()
    {
        return reorderChildContents;
    }

    public boolean stopInherit()
    {
        return stopInherit;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentId contentId;

        private ChildOrder childOrder;

        private ChildOrder manualOrderSeed;

        private final ImmutableList.Builder<ReorderChildContentParams> reorderChildContents = ImmutableList.builder();

        private boolean stopInherit = true;

        private Builder()
        {
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
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

        public Builder addManualOrder( final ReorderChildContentParams reorderChildParams )
        {
            this.reorderChildContents.add( reorderChildParams );
            return this;
        }


        public Builder stopInherit( final boolean stopInherit )
        {
            this.stopInherit = stopInherit;
            return this;
        }

        public SortContentParams build()
        {
            return new SortContentParams( this );
        }
    }
}
