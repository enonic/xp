package com.enonic.xp.content;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class ReorderChildContentsParams
    extends AbstractImmutableEntityList<ReorderChildParams>
{
    private final ContentId contentId;

    private final boolean stopInherit;

    private ReorderChildContentsParams( final Builder builder )
    {
        super( builder.orderChildContentParamsList.build() );
        contentId = builder.contentId;
        stopInherit = builder.stopInherit;
    }

    public ContentId getContentId()
    {
        return contentId;
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
