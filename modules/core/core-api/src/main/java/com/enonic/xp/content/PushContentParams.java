package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public class PushContentParams
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeChildrenIds;

    private final ContentPublishInfo contentPublishInfo;

    private final boolean includeChildren;

    private final boolean includeDependencies;

    private final PushContentListener publishContentListener;

    private final DeleteContentListener deleteContentListener;

    private final String message;

    private PushContentParams( Builder builder )
    {
        contentIds = builder.contentIds;
        contentPublishInfo = builder.contentPublishInfo;
        includeDependencies = builder.includeDependencies;
        excludeChildrenIds = builder.excludeChildrenIds;
        includeChildren = builder.includeChildren;
        publishContentListener = builder.publishContentListener;
        deleteContentListener = builder.deleteContentListener;
        message = builder.message;
        excludedContentIds = builder.excludedContentIds.isEmpty()
            ? ( builder.includeChildren ? ContentIds.empty() : builder.contentIds )
            : builder.excludedContentIds;
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

    @Deprecated
    public Branch getTarget()
    {
        return ContentConstants.BRANCH_MASTER;
    }

    public ContentPublishInfo getContentPublishInfo()
    {
        return contentPublishInfo;
    }

    @Deprecated
    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public ContentIds getExcludeChildrenIds()
    {
        return excludeChildrenIds;
    }

    public boolean isIncludeDependencies()
    {
        return includeDependencies;
    }

    public PushContentListener getPublishContentListener()
    {
        return publishContentListener;
    }

    public DeleteContentListener getDeleteContentListener()
    {
        return deleteContentListener;
    }

    public String getMessage()
    {
        return message;
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

    public static final class Builder
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds = ContentIds.empty();

        private ContentIds excludeChildrenIds = ContentIds.empty();

        private ContentPublishInfo contentPublishInfo;

        private boolean includeChildren = true;

        private boolean includeDependencies = true;

        private PushContentListener publishContentListener;

        private DeleteContentListener deleteContentListener;

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

        public Builder excludeChildrenIds( ContentIds excludeChildrenIds )
        {
            this.excludeChildrenIds = excludeChildrenIds;
            return this;
        }

        @Deprecated
        public Builder target( Branch target )
        {
            return this;
        }

        public Builder contentPublishInfo( ContentPublishInfo contentPublishInfo )
        {
            this.contentPublishInfo = contentPublishInfo;
            return this;
        }

        @Deprecated
        public Builder includeChildren( boolean includeChildren )
        {
            this.includeChildren = includeChildren;
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

        public Builder deleteContentListener( final DeleteContentListener deleteContentListener )
        {
            this.deleteContentListener = deleteContentListener;
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
