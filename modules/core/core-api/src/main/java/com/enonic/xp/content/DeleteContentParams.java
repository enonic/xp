package com.enonic.xp.content;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public final class DeleteContentParams
{
    private final ContentPath contentPath;

    private final boolean deleteOnline;

    private final DeleteContentListener deleteContentListener;

    private DeleteContentParams( Builder builder )
    {
        contentPath = builder.contentPath;
        deleteOnline = builder.deleteOnline;
        deleteContentListener = builder.deleteContentListener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public boolean isDeleteOnline()
    {
        return deleteOnline;
    }

    public DeleteContentListener getDeleteContentListener()
    {
        return deleteContentListener;
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

        private boolean deleteOnline = false;

        private DeleteContentListener deleteContentListener;

        private Builder()
        {
        }

        public Builder contentPath( ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public Builder deleteOnline( boolean deleteOnline )
        {
            this.deleteOnline = deleteOnline;
            return this;
        }

        public Builder deleteContentListener( final DeleteContentListener deleteContentListener )
        {
            this.deleteContentListener = deleteContentListener;
            return this;
        }

        public DeleteContentParams build()
        {
            return new DeleteContentParams( this );
        }
    }
}
