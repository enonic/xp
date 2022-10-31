package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;

public final class GetActiveContentVersionParams
{
    private final ContentId contentId;

    private final Branch branch;

    private GetActiveContentVersionParams( final Builder builder )
    {
        this.contentId = builder.contentId;
        this.branch = builder.branch;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private ContentId contentId;

        private Branch branch;

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        @Deprecated
        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public GetActiveContentVersionParams build()
        {
            Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
            return new GetActiveContentVersionParams( this );
        }
    }
}
