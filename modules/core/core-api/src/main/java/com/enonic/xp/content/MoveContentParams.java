package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class MoveContentParams
{
    private final ContentId contentId;

    private final ContentPath parentContentPath;

    private final PrincipalKey creator;

    private final MoveContentListener moveContentListener;

    private final boolean stopInherit;

    public MoveContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.parentContentPath = builder.parentContentPath;
        this.creator = builder.creator;
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

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public MoveContentListener getMoveContentListener()
    {
        return moveContentListener;
    }

    public boolean stopInherit()
    {
        return stopInherit;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
    }

    public static final class Builder
    {

        private ContentId contentId;

        private ContentPath parentContentPath;

        private PrincipalKey creator;

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

        public Builder creator( PrincipalKey creator )
        {
            this.creator = creator;
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
            return new MoveContentParams( this );
        }
    }
}
