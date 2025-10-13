package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class MoveContentParams
{
    private final ContentId contentId;

    private final ContentPath parentContentPath;

    private final ContentName newName;

    private final MoveContentListener moveContentListener;

    private final boolean stopInherit;

    private MoveContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.parentContentPath = builder.parentContentPath;
        this.newName = builder.newName;
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

    public ContentName getNewName()
    {
        return newName;
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

        private ContentName newName;

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

        public Builder newName( ContentName newName )
        {
            this.newName = newName;
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
            Objects.requireNonNull( this.contentId, "contentId is required" );
            return new MoveContentParams( this );
        }
    }
}
