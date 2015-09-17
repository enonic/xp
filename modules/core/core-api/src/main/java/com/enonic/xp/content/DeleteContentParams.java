package com.enonic.xp.content;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public final class DeleteContentParams
{
    private final ContentPath contentPath;

    private DeleteContentParams( Builder builder )
    {
        contentPath = builder.contentPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentPath getContentPath()
    {
        return contentPath;
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

    public static final class Builder
    {
        private ContentPath contentPath;

        private Builder()
        {
        }

        public Builder contentPath( ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public DeleteContentParams build()
        {
            return new DeleteContentParams( this );
        }
    }
}
