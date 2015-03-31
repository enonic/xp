package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.security.PrincipalKey;

public final class MoveContentParams
{
    private ContentIds contentIds;

    private ContentPath parentContentPath;

    private PrincipalKey creator;

    public MoveContentParams( final ContentIds contentIds, final ContentPath parentContentPath )
    {
        this.contentIds = contentIds;
        this.parentContentPath = parentContentPath;

    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public ContentPath getParentContentPath()
    {
        return parentContentPath;
    }

    public MoveContentParams creator( final PrincipalKey creator )
    {
        this.creator = creator;
        return this;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentIds, "Content ids cannot be null" );
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

        if ( !contentIds.equals( that.contentIds ) )
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
        int result = contentIds != null ? contentIds.hashCode() : 0;
        result = 31 * result + ( parentContentPath != null ? parentContentPath.hashCode() : 0 );
        return result;
    }
}
