package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class PushContentParams
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeChildrenIds;

    private final Branch target;

    private final ContentPublishInfo contentPublishInfo;

    private final boolean includeChildren;

    private final boolean includeDependencies;

    private final PushContentListener pushContentListener;

    private PushContentParams( Builder builder )
    {
        contentIds = builder.contentIds;
        excludedContentIds = builder.excludedContentIds;
        target = builder.target;
        contentPublishInfo = builder.contentPublishInfo;
        includeDependencies = builder.includeDependencies;
        excludeChildrenIds = builder.excludeChildrenIds;
        includeChildren = builder.includeChildren;
        pushContentListener = builder.pushContentListener;
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

    public Branch getTarget()
    {
        return target;
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

    public PushContentListener getPushContentListener()
    {
        return pushContentListener;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final PushContentParams that = (PushContentParams) o;
        return includeChildren == that.includeChildren && includeDependencies == that.includeDependencies &&
            Objects.equals( excludeChildrenIds, that.excludeChildrenIds ) && Objects.equals( contentIds, that.contentIds ) &&
            Objects.equals( excludedContentIds, that.excludedContentIds ) && Objects.equals( target, that.target ) &&
            Objects.equals( pushContentListener, that.pushContentListener );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentIds, excludedContentIds, includeChildren, excludeChildrenIds, target, includeDependencies,
                             pushContentListener );
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeChildrenIds = ContentIds.empty();

        private Branch target;

        private ContentPublishInfo contentPublishInfo;

        private boolean includeChildren = true;

        private boolean includeDependencies = true;

        private PushContentListener pushContentListener;

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

        public Builder target( Branch target )
        {
            this.target = target;
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

        public Builder pushListener( final PushContentListener pushContentListener )
        {
            this.pushContentListener = pushContentListener;
            return this;
        }

        public PushContentParams build()
        {
            return new PushContentParams( this );
        }
    }
}