package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.BranchId;

public class UnpublishContentParams
{
    private final ContentIds contentIds;

    private final boolean includeChildren;

    private final BranchId unpublishBranchId;

    private UnpublishContentParams( final Builder builder )
    {
        contentIds = builder.contentIds;
        unpublishBranchId = builder.unpublishBranchId;
        includeChildren = builder.includeChildren;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public BranchId getUnpublishBranchId()
    {
        return unpublishBranchId;
    }

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentIds contentIds;

        private BranchId unpublishBranchId;

        private boolean includeChildren;

        private Builder()
        {
        }

        public Builder contentIds( final ContentIds val )
        {
            contentIds = val;
            return this;
        }

        public Builder unpublishBranch( final BranchId val )
        {
            unpublishBranchId = val;
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
            Preconditions.checkNotNull( unpublishBranchId, "unpublish-branch must be set" );
        }

        public UnpublishContentParams build()
        {
            this.validate();
            return new UnpublishContentParams( this );
        }
    }
}
