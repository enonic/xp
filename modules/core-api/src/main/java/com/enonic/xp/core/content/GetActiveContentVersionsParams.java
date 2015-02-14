package com.enonic.xp.core.content;

import com.enonic.xp.core.branch.Branches;

public class GetActiveContentVersionsParams
{
    private final ContentId contentId;

    private final Branches branches;

    private GetActiveContentVersionsParams( Builder builder )
    {
        contentId = builder.contentId;
        branches = builder.branches;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private Branches branches;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder branches( Branches branches )
        {
            this.branches = branches;
            return this;
        }

        public GetActiveContentVersionsParams build()
        {
            return new GetActiveContentVersionsParams( this );
        }
    }
}
