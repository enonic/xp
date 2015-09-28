package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class ResolvePublishDependenciesParams
{
    private final ContentIds contentIds;

    private final Branch target;

    private final boolean includeChildren;

    private ResolvePublishDependenciesParams( Builder builder )
    {
        contentIds = builder.contentIds;
        target = builder.target;
        includeChildren = builder.includeChildren;
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
        return target;
    }

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Branch target;

        private boolean includeChildren = true;

        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder target( Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder includeChildren( boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        public ResolvePublishDependenciesParams build()
        {
            return new ResolvePublishDependenciesParams( this );
        }
    }
}