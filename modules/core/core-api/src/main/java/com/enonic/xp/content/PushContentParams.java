package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PushContentParams
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeDescendantsOf;

    private final ContentPublishInfo contentPublishInfo;

    private final boolean includeDependencies;

    private final PushContentListener publishContentListener;

    private final String message;

    private PushContentParams( Builder builder )
    {
        this.contentIds = builder.contentIds;
        this.contentPublishInfo = builder.contentPublishInfo;
        this.includeDependencies = builder.includeDependencies;
        this.excludeDescendantsOf = builder.excludeDescendantsOf;
        this.publishContentListener = builder.publishContentListener;
        this.message = builder.message;
        this.excludedContentIds = builder.excludedContentIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public ContentIds getExcludedContentIds()
    {
        return excludedContentIds;
    }

    public ContentPublishInfo getContentPublishInfo()
    {
        return contentPublishInfo;
    }

    public ContentIds getExcludeDescendantsOf()
    {
        return excludeDescendantsOf;
    }

    public boolean isIncludeDependencies()
    {
        return includeDependencies;
    }

    public PushContentListener getPublishContentListener()
    {
        return publishContentListener;
    }

    public String getMessage()
    {
        return message;
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeDescendantsOf;

        private ContentPublishInfo contentPublishInfo;

        private boolean includeDependencies = true;

        private PushContentListener publishContentListener;

        private String message;

        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder excludedContentIds( ContentIds excludedContentIds )
        {
            this.excludedContentIds = excludedContentIds;
            return this;
        }

        public Builder excludeDescendantsOf( ContentIds excludeDescendantsOf )
        {
            this.excludeDescendantsOf = excludeDescendantsOf;
            return this;
        }

        public Builder contentPublishInfo( ContentPublishInfo contentPublishInfo )
        {
            this.contentPublishInfo = contentPublishInfo;
            return this;
        }

        public Builder includeDependencies( final boolean includeDependencies )
        {
            this.includeDependencies = includeDependencies;
            return this;
        }

        public Builder pushListener( final PushContentListener publishContentListener )
        {
            this.publishContentListener = publishContentListener;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public PushContentParams build()
        {
            return new PushContentParams( this );
        }
    }
}
