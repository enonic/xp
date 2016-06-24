package com.enonic.xp.content;

import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

@Beta
public class UnpublishContentsResult
{
    private final ContentIds unpublishedContents;

    private final String contentName;

    private UnpublishContentsResult( Builder builder )
    {
        this.unpublishedContents = ContentIds.from( builder.unpublishedContents );
        this.contentName = builder.contentName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getUnpublishedContents()
    {
        return unpublishedContents;
    }

    public String getContentName()
    {
        return contentName;
    }

    public static final class Builder
    {
        private List<ContentId> unpublishedContents = Lists.newArrayList();

        private String contentName;

        private Builder()
        {
        }

        public Builder addUnpublished(final ContentId contentId) {
            this.unpublishedContents.add( contentId );
            return this;
        }

        public Builder addUnpublished(final ContentIds contentIds) {
            this.unpublishedContents.addAll( contentIds.getSet() );
            return this;
        }

        public Builder setContentName( final String contentName )
        {
            this.contentName = contentName;
            return this;
        }

        public UnpublishContentsResult build()
        {
            return new UnpublishContentsResult( this );
        }
    }
}