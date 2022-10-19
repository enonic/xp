package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public class ResolvePublishDependenciesParams
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeChildrenIds;

    private ResolvePublishDependenciesParams( Builder builder )
    {
        contentIds = builder.contentIds;
        excludeChildrenIds = builder.excludeChildrenIds;
        excludedContentIds = builder.excludedContentIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public ContentIds getExcludedContentIds()
    {
        return excludedContentIds;
    }

    public ContentIds getExcludeChildrenIds()
    {
        return excludeChildrenIds;
    }

    @Deprecated
    public Branch getTarget()
    {
        return ContentConstants.BRANCH_MASTER;
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeChildrenIds;

        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder excludedContentIds( ContentIds excludedContentIds )
        {
            this.excludedContentIds = excludedContentIds;
            return this;
        }

        public Builder excludeChildrenIds( ContentIds excludeChildrenIds )
        {
            this.excludeChildrenIds = excludeChildrenIds;
            return this;
        }

        @Deprecated
        public Builder target( Branch target )
        {
            return this;
        }

        public ResolvePublishDependenciesParams build()
        {
            return new ResolvePublishDependenciesParams( this );
        }
    }
}
