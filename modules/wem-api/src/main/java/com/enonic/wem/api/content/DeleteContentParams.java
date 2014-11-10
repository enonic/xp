package com.enonic.wem.api.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;

public final class DeleteContentParams
{
    private ContentPath contentPath;

    private UserKey deleter;

    public UserKey getDeleter()
    {
        return deleter;
    }

    public DeleteContentParams contentPath( final ContentPath contentPath )
    {
        this.contentPath = contentPath;
        return this;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public DeleteContentParams deleter( final UserKey deleter )
    {
        this.deleter = deleter;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentPath, "ContentPath cannot be null" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof DeleteContentParams ) )
        {
            return false;
        }

        final DeleteContentParams that = (DeleteContentParams) o;

        if ( !contentPath.equals( that.contentPath ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return contentPath.hashCode();
    }
}
