package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.BranchIds;

@Beta
public class GetActiveContentVersionsParams
{
    private final ContentId contentId;

    private final BranchIds branchIds;

    private GetActiveContentVersionsParams( Builder builder )
    {
        contentId = builder.contentId;
        branchIds = builder.branchIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public BranchIds getBranchIds()
    {
        return branchIds;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private BranchIds branchIds;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder branches( BranchIds branchIds )
        {
            this.branchIds = branchIds;
            return this;
        }

        public GetActiveContentVersionsParams build()
        {
            return new GetActiveContentVersionsParams( this );
        }
    }
}
