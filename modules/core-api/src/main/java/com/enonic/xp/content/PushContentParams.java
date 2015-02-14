package com.enonic.xp.content;

import com.enonic.xp.branch.Branch;

public class PushContentParams
{
    private final ContentIds contentIds;

    private final Branch target;

    private final boolean includeChildren;

    private final boolean allowPublishOutsideSelection;

    private PushContentParams( Builder builder )
    {
        contentIds = builder.contentIds;
        target = builder.target;
        includeChildren = builder.includeChildren;
        allowPublishOutsideSelection = builder.allowPublishOutsideSelection;
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

    public static final class Builder
    {
        private ContentIds contentIds;

        private Branch target;

        private boolean includeChildren = true;

        private boolean allowPublishOutsideSelection = true;

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

        public PushContentParams build()
        {
            return new PushContentParams( this );
        }
    }
}
