package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class PushContentParams
{
    private final ContentIds contentIds;

    private final Branch target;

    private final boolean includeChildren;

    private final boolean allowPublishOutsideSelection;

    private final boolean resolveDependencies;

    private PushContentParams( Builder builder )
    {
        contentIds = builder.contentIds;
        target = builder.target;
        includeChildren = builder.includeChildren;
        allowPublishOutsideSelection = builder.allowPublishOutsideSelection;
        resolveDependencies = builder.resolveDependencies;
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

    public boolean isAllowPublishOutsideSelection()
    {
        return allowPublishOutsideSelection;
    }

    public boolean isResolveDependencies()
    {
        return resolveDependencies;
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Branch target;

        private boolean includeChildren = true;

        private boolean allowPublishOutsideSelection = true;

        private boolean resolveDependencies = true;

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

        public Builder allowPublishOutsideSelection( boolean allowPublishOutsideSelection )
        {
            this.allowPublishOutsideSelection = allowPublishOutsideSelection;
            return this;
        }

        public Builder resolveDependencies( final boolean resolveDependencies )
        {
            this.resolveDependencies = resolveDependencies;
            return this;
        }

        public PushContentParams build()
        {
            return new PushContentParams( this );
        }
    }
}
