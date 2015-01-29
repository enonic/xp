package com.enonic.wem.api.content;

import com.enonic.wem.api.workspace.Workspace;

public class PushContentParams
{
    private final ContentIds contentIds;

    private final Workspace target;

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

    public Workspace getTarget()
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

        private Workspace target;

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

        public Builder target( Workspace target )
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
