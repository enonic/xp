package com.enonic.wem.api.content;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.entity.Workspace;

public class GetActiveContentVersionsParams
{
    private final ContentId contentId;

    private final ImmutableSet<Workspace> workspaces;

    private GetActiveContentVersionsParams( Builder builder )
    {
        contentId = builder.contentId;
        workspaces = ImmutableSet.copyOf( builder.workspaces );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ImmutableSet<Workspace> getWorkspaces()
    {
        return workspaces;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private Set<Workspace> workspaces = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder workspaces( Set<Workspace> workspaces )
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
