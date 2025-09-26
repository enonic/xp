package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class UnpublishContentsResult
{
    private final ContentIds unpublishedContents;

    private UnpublishContentsResult( Builder builder )
    {
        this.unpublishedContents = builder.unpublishedContents.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getUnpublishedContents()
    {
        return unpublishedContents;
    }

    public static final class Builder
    {
        private final ContentIds.Builder unpublishedContents = ContentIds.create();

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

        public UnpublishContentsResult build()
        {
            return new UnpublishContentsResult( this );
        }
    }
}
