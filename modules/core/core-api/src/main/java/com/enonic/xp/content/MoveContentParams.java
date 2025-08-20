package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class MoveContentParams
{
    private final ContentId contentId;

    private final ContentPath parentContentPath;

    private final MoveContentListener moveContentListener;

    private final boolean stopInherit;

    private MoveContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.parentContentPath = builder.parentContentPath;
        this.moveContentListener = builder.moveContentListener;
        this.stopInherit = builder.stopInherit;
    }

    public static MoveContentParams.Builder create()
    {
        return new MoveContentParams.Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentPath getParentContentPath()
    {
        return parentContentPath;
    }

    public MoveContentListener getMoveContentListener()
    {
        return moveContentListener;
    }

    public boolean stopInherit()
    {
        return stopInherit;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private ContentPath parentContentPath;

        private MoveContentListener moveContentListener;

        private boolean stopInherit = true;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder parentContentPath( ContentPath parentContentPath )
        {
            this.parentContentPath = parentContentPath;
            return this;
        }

        public Builder moveContentListener( MoveContentListener moveContentListener )
        {
            this.moveContentListener = moveContentListener;
            return this;
        }

        public Builder stopInherit( boolean stopInherit )
        {
            this.stopInherit = stopInherit;
            return this;
        }

        public MoveContentParams build()
        {
            Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
            return new MoveContentParams( this );
        }
    }
}
