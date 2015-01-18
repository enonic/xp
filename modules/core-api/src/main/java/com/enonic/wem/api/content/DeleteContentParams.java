package com.enonic.wem.api.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.security.PrincipalKey;

public final class DeleteContentParams
{
    private ContentPath contentPath;

    private PrincipalKey deleter;

    public PrincipalKey getDeleter()
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

    public DeleteContentParams deleter( final PrincipalKey deleter )
    {
        this.deleter = deleter;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentPath, "ContentPath cannot be null" );
        Preconditions.checkNotNull( this.contentPath.isAbsolute(), "ContentPath must be absolute: " + this.contentPath );
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
