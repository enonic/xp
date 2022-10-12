package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;

public final class UnpublishContentParams
{
    private final ContentIds contentIds;

    private final Branch unpublishBranch;

    private final PushContentListener publishContentListener;

    private UnpublishContentParams( final Builder builder )
    {
        contentIds = builder.contentIds;
        unpublishBranch = builder.unpublishBranch;
        publishContentListener = builder.publishContentListener;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getUnpublishBranch()
    {
        return unpublishBranch;
    }

    @Deprecated
    public boolean isIncludeChildren()
    {
        return true;
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

    public static Builder create()
    {
        return new Builder();
    }

    public PushContentListener getPublishContentListener()
    {
        return publishContentListener;
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Branch unpublishBranch;

        private PushContentListener publishContentListener;

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

        @Deprecated
        public Builder includeChildren( final boolean val )
        {
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentIds, "contentId must be set" );
            Preconditions.checkNotNull( unpublishBranch, "unpublishBranch must be set" );
        }

        public Builder pushListener( final PushContentListener publishContentListener )
        {
            this.publishContentListener = publishContentListener;
            return this;
        }

        public UnpublishContentParams build()
        {
            this.validate();
            return new UnpublishContentParams( this );
        }
    }
}
