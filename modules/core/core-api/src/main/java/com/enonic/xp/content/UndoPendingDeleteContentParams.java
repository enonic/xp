package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public final class UndoPendingDeleteContentParams
{
    private final ContentIds contentIds;

    private final Branch target;

    private UndoPendingDeleteContentParams( final Builder builder )
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

        public Builder contentIds( final ContentIds val )
        {
            contentIds = val;
            return this;
        }

        public Builder target( final Branch val )
        {
            target = val;
            return this;
        }

        public UndoPendingDeleteContentParams build()
        {
            return new UndoPendingDeleteContentParams( this );
        }
    }
}
