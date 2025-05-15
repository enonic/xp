package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class HasUnpublishedChildrenParams
{
    private final ContentId contentId;

    private HasUnpublishedChildrenParams( final ContentId contentId )
    {
        this.contentId = contentId;
    }

    public ContentId getContentId()
    {
        return contentId;
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
        private ContentId contentId;

        private Builder()
        {
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public HasUnpublishedChildrenParams build()
        {
            Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
            return new HasUnpublishedChildrenParams( this.contentId );
        }
    }
}
