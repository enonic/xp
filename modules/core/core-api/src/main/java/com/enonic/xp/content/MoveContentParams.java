package com.enonic.xp.content;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.security.PrincipalKey;

@Beta
public final class MoveContentParams
{
    private ContentId contentId;

    private ContentPath parentContentPath;

    private PrincipalKey creator;

    private final MoveContentListener moveContentListener;

    public MoveContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.parentContentPath = builder.parentContentPath;
        this.creator = builder.creator;
        this.moveContentListener = builder.moveContentListener;
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

    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof MoveContentParams ) )
        {
            return false;
        }

        final MoveContentParams that = (MoveContentParams) o;

        if ( !contentId.equals( that.contentId ) )
        {
            return false;
        }
        else if ( !parentContentPath.equals( that.parentContentPath ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = contentId != null ? contentId.hashCode() : 0;
        result = 31 * result + ( parentContentPath != null ? parentContentPath.hashCode() : 0 );
        return result;
    }

    public static final class Builder
    {

        private ContentId contentId;

        private ContentPath parentContentPath;

        private PrincipalKey creator;

        private MoveContentListener moveContentListener;

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

        public MoveContentParams build()
        {
            return new MoveContentParams( this );
        }
    }
}
