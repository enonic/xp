package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class PushContentParams
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final Branch target;

    private final boolean includeChildren;

    private final boolean includeDependencies;

    private PushContentParams( Builder builder )
    {
        contentIds = builder.contentIds;
        excludedContentIds = builder.excludedContentIds;
        target = builder.target;
        includeDependencies = builder.includeDependencies;
        includeChildren = builder.includeChildren;
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

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public boolean isIncludeDependencies()
    {
        return includeDependencies;
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
        return includeChildren == that.includeChildren &&
            includeDependencies == that.includeDependencies &&
            Objects.equals( contentIds, that.contentIds ) &&
            Objects.equals( excludedContentIds, that.excludedContentIds ) &&
            Objects.equals( target, that.target );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentIds, excludedContentIds, target, includeChildren, includeDependencies );
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private Branch target;

        private boolean includeChildren = true;

        private boolean includeDependencies = true;

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

        public Builder target( Branch target )
        {
            this.target = target;
            return this;
        }

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

        public PushContentParams build()
        {
            return new PushContentParams( this );
        }
    }
}