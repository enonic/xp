package com.enonic.xp.content;

import static java.util.Objects.requireNonNull;


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
            requireNonNull( this.contentIds, "contentIds is required" );
            return new CompareContentsParams( this );
        }
    }
}
