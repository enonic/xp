package com.enonic.xp.content;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public class ReorderChildContentsParams
    extends AbstractImmutableEntityList<ReorderChildParams>
{
    private final ContentId contentId;

    private final boolean silent;

    private final boolean stopInherit;

    private ReorderChildContentsParams( final Builder builder )
    {
        super( builder.orderChildContentParamsList.build() );
        contentId = builder.contentId;
        silent = builder.silent;
        stopInherit = builder.stopInherit;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public boolean isSilent()
    {
        return silent;
    }

    public boolean stopInherit()
    {
        return stopInherit;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static class Builder
    {
        private final ImmutableList.Builder<ReorderChildParams> orderChildContentParamsList = ImmutableList.builder();

        private ContentId contentId;

        private boolean silent;

        private boolean stopInherit = true;

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

        public Builder silent( final boolean silent )
        {
            this.silent = silent;
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
