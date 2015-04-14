package com.enonic.xp.content;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.security.PrincipalKey;

@Beta
public final class DuplicateContentParams
{
    private ContentId contentId;

    private PrincipalKey creator;

    public DuplicateContentParams( final ContentId contentId )
    {
        this.contentId = contentId;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public DuplicateContentParams creator( final PrincipalKey creator )
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
        if ( !( o instanceof DuplicateContentParams ) )
        {
            return false;
        }

        final DuplicateContentParams that = (DuplicateContentParams) o;

        if ( !contentId.equals( that.contentId ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return contentId.hashCode();
    }
}
