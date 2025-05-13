package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class UnpublishContentsResult
{
    private final ContentIds unpublishedContents;

    private final ContentPath contentPath;

    private UnpublishContentsResult( Builder builder )
    {
        this.unpublishedContents = builder.unpublishedContents.build();
        this.contentPath = builder.contentPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getUnpublishedContents()
    {
        return unpublishedContents;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public static final class Builder
    {
        private final ContentIds.Builder unpublishedContents = ContentIds.create();

        private ContentPath contentPath;

        private Builder()
        {
        }

        public Builder addUnpublished(final ContentId contentId) {
            this.unpublishedContents.add( contentId );
            return this;
        }

        public Builder addUnpublished(final ContentIds contentIds) {
            this.unpublishedContents.addAll( contentIds );
            return this;
        }

        public Builder setContentPath( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public UnpublishContentsResult build()
        {
            return new UnpublishContentsResult( this );
        }
    }
}
