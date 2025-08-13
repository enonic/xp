package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class CompareContentsParams
{
    private final ContentIds contentIds;

    private CompareContentsParams( final Builder builder )
    {
        this.contentIds = builder.contentIds;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
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

        public CompareContentsParams build()
        {
            Preconditions.checkNotNull( this.contentIds, "Content ids cannot be null" );
            return new CompareContentsParams( this );
        }
    }
}
