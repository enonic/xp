package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;

public class UnpublishContentParams
{
    private final ContentId contentId;

    private final Branch unpublishBranch;

    private UnpublishContentParams( final Builder builder )
    {
        contentId = builder.contentId;
        unpublishBranch = builder.unpublishBranch;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Branch getUnpublishBranch()
    {
        return unpublishBranch;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentId contentId;

        private Branch unpublishBranch;

        private Builder()
        {
        }

        public Builder contentId( final ContentId val )
        {
            contentId = val;
            return this;
        }

        public Builder unpublishBranch( final Branch val )
        {
            unpublishBranch = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentId, "contentId must be set" );
            Preconditions.checkNotNull( unpublishBranch, "unpublish-branch must be set" );
        }

        public UnpublishContentParams build()
        {
            this.validate();
            return new UnpublishContentParams( this );
        }
    }
}
