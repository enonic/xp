package com.enonic.wem.api.content;

import com.enonic.wem.api.workspace.Workspaces;

public class GetActiveContentVersionsParams
{
    private final ContentId contentId;

    private final Workspaces workspaces;

    private GetActiveContentVersionsParams( Builder builder )
    {
        contentId = builder.contentId;
        workspaces = builder.workspaces;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Workspaces getWorkspaces()
    {
        return workspaces;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private Workspaces workspaces;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder workspaces( Workspaces workspaces )
        {
            this.workspaces = workspaces;
            return this;
        }

        public GetActiveContentVersionsParams build()
        {
            return new GetActiveContentVersionsParams( this );
        }
    }
}
