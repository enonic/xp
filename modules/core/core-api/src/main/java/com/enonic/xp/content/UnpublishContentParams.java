package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;

public final class UnpublishContentParams
{
    private final ContentIds contentIds;

    private final PushContentListener publishContentListener;

    private UnpublishContentParams( final Builder builder )
    {
        contentIds = builder.contentIds;
        publishContentListener = builder.publishContentListener;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public PushContentListener getPublishContentListener()
    {
        return publishContentListener;
    }

    @Deprecated
    public Branch getUnpublishBranch()
    {
        return ContentConstants.BRANCH_MASTER;
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

    public static final class Builder
    {
        private ContentIds contentIds;

        private PushContentListener publishContentListener;

        private Builder()
        {
        }

        public Builder contentIds( final ContentIds val )
        {
            contentIds = val;
            return this;
        }

        @Deprecated
        public Builder unpublishBranch( final Branch val )
        {
            return this;
        }

        @Deprecated
        public Builder includeChildren( final boolean val )
        {
            return this;
        }

        public Builder pushListener( final PushContentListener publishContentListener )
        {
            this.publishContentListener = publishContentListener;
            return this;
        }

        public UnpublishContentParams build()
        {
            Preconditions.checkNotNull( contentIds, "contentId must be set" );
            return new UnpublishContentParams( this );
        }
    }
}
