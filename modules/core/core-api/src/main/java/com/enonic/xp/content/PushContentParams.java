package com.enonic.xp.content;

import java.time.Instant;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PushContentParams
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeDescendantsOf;

    private final Instant publishFrom;

    private final Instant publishTo;

    private final boolean includeDependencies;

    private final PushContentListener publishContentListener;

    private final String message;

    private PushContentParams( Builder builder )
    {
        this.contentIds = builder.contentIds;
        this.publishFrom = builder.publishFrom;
        this.publishTo = builder.publishTo;
        this.includeDependencies = builder.includeDependencies;
        this.excludeDescendantsOf = Objects.requireNonNullElse( builder.excludeDescendantsOf, ContentIds.empty() );
        this.publishContentListener = builder.publishContentListener;
        this.message = builder.message;
        this.excludedContentIds = Objects.requireNonNullElse( builder.excludedContentIds, ContentIds.empty() );
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

    public Instant getPublishFrom()
    {
        return publishFrom;
    }

    public Instant getPublishTo()
    {
        return publishTo;
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

        private Instant publishFrom;

        private Instant publishTo;

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

        public Builder publishFrom( final Instant publishFrom )
        {
            this.publishFrom = publishFrom;
            return this;
        }

        public Builder publishTo( final Instant publishTo )
        {
            this.publishTo = publishTo;
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
            Preconditions.checkArgument( contentIds != null && !contentIds.isEmpty(), "contentIds is required" );
            return new PushContentParams( this );
        }
    }
}
