package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class ResolveOfflineContentsParams
{
    private final ContentIds contentIds;

    private final Branch target;

    private ResolveOfflineContentsParams( Builder builder )
    {
        contentIds = builder.contentIds;
        target = builder.target;
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

    public static final class Builder
    {
        private ContentIds contentIds;

        private Branch target;

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

        public ResolveOfflineContentsParams build()
        {
            return new ResolveOfflineContentsParams( this );
        }
    }
}