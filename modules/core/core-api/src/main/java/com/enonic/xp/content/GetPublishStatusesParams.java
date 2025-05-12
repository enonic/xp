package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public final class GetPublishStatusesParams
{
    private final ContentIds contentIds;

    private final Branch target;

    private GetPublishStatusesParams( final ContentIds contentIds )
    {
        this.contentIds = contentIds;
        this.target = null;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getTarget()
    {
        return target;
    }

    @Override
    public boolean equals( final Object o )
    {
        return super.equals( o );
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Builder()
        {
        }

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public GetPublishStatusesParams build()
        {
            Preconditions.checkNotNull( this.contentIds, "Content ids cannot be null" );
            return new GetPublishStatusesParams( this.contentIds );
        }
    }
}
