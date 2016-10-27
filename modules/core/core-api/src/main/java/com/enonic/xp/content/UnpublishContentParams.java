package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;

public final class UnpublishContentParams
{
    private final ContentIds contentIds;

    private final boolean includeChildren;

    private final Branch unpublishBranch;

    private final PushContentListener pushContentListener;

    private UnpublishContentParams( final Builder builder )
    {
        contentIds = builder.contentIds;
        unpublishBranch = builder.unpublishBranch;
        includeChildren = builder.includeChildren;
        pushContentListener = builder.pushContentListener;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getUnpublishBranch()
    {
        return unpublishBranch;
    }

    public boolean isIncludeChildren()
    {
        return includeChildren;
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
        final UnpublishContentParams that = (UnpublishContentParams) o;
        return includeChildren == that.includeChildren && Objects.equals( contentIds, that.contentIds ) &&
            Objects.equals( unpublishBranch, that.unpublishBranch );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentIds, includeChildren, unpublishBranch );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PushContentListener getPushContentListener()
    {
        return pushContentListener;
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Branch unpublishBranch;

        private boolean includeChildren;

        private PushContentListener pushContentListener;

        private Builder()
        {
        }

        public Builder contentIds( final ContentIds val )
        {
            contentIds = val;
            return this;
        }

        public Builder unpublishBranch( final Branch val )
        {
            unpublishBranch = val;
            return this;
        }

        public Builder includeChildren( final boolean val )
        {
            includeChildren = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentIds, "contentId must be set" );
            Preconditions.checkNotNull( unpublishBranch, "unpublishBranch must be set" );
        }

        public Builder pushListener( final PushContentListener pushContentListener )
        {
            this.pushContentListener = pushContentListener;
            return this;
        }

        public Builder pushListener( final PushContentListener pushContentListener )
        {
            this.pushContentListener = pushContentListener;
            return this;
        }

        public UnpublishContentParams build()
        {
            this.validate();
            return new UnpublishContentParams( this );
        }
    }
}
