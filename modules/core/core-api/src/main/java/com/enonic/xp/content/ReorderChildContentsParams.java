package com.enonic.xp.content;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ReorderChildContentsParams
    implements Iterable<ReorderChildParams>
{
    private final ContentId contentId;

    private final boolean stopInherit;

    private final ImmutableList<ReorderChildParams> list;

    private ReorderChildContentsParams( final Builder builder )
    {
        this.list = builder.orderChildContentParamsList.build();
        this.contentId = builder.contentId;
        this.stopInherit = builder.stopInherit;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public boolean stopInherit()
    {
        return stopInherit;
    }

    public Iterator<ReorderChildParams> iterator()
    {
        return list.iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ReorderChildParams> orderChildContentParamsList = ImmutableList.builder();

        private ContentId contentId;

        private boolean stopInherit = true;

        private Builder()
        {
        }

        public Builder add( final ReorderChildParams orderChildNodeParams )
        {
            this.orderChildContentParamsList.add( orderChildNodeParams );
            return this;
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder stopInherit( final boolean stopInherit )
        {
            this.stopInherit = stopInherit;
            return this;
        }

        public ReorderChildContentsParams build()
        {
            return new ReorderChildContentsParams( this );
        }

    }

}
