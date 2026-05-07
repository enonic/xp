package com.enonic.xp.content;

import com.enonic.xp.branch.Branch;

import static java.util.Objects.requireNonNull;


public final class ResolveRequiredDependenciesParams
{
    private final ContentIds contentIds;

    private ResolveRequiredDependenciesParams( Builder builder )
    {
        contentIds = builder.contentIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getTarget()
    {
        return ContentConstants.BRANCH_MASTER;
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public ResolveRequiredDependenciesParams build()
        {
            requireNonNull( this.contentIds, "contentIds is required" );
            return new ResolveRequiredDependenciesParams( this );
        }
    }
}
