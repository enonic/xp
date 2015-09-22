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

    public MoveContentParams( final ContentId contentId, final ContentPath parentContentPath )
    {
        this.contentId = contentId;
        this.parentContentPath = parentContentPath;

    }

    public ContentId getContentId()
    {
        return contentId;
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
}
