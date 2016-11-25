package com.enonic.xp.content;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public final class DeleteContentsParams
{
    private final ContentPaths contentPaths;

    private final boolean deleteOnline;

    private final DeleteContentListener callback;

    private DeleteContentsParams( Builder builder )
    {
        contentPaths = builder.contentPaths;
        deleteOnline = builder.deleteOnline;
        callback = builder.callback;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentPaths getContentPaths()
    {
        return contentPaths;
    }

    public boolean isDeleteOnline()
    {
        return deleteOnline;
    }

    public DeleteContentListener getCallback()
    {
        return callback;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentPaths, "ContentPath cannot be null" );
        Preconditions.checkNotNull( this.contentPaths.stream().allMatch( path -> path.isAbsolute() ),
                                    "ContentPath must be absolute: " + this.contentPaths );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof DeleteContentsParams ) )
        {
            return false;
        }

        final DeleteContentsParams that = (DeleteContentsParams) o;

        if ( !contentPaths.equals( that.contentPaths ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return contentPaths.hashCode();
    }

    public static final class Builder
    {
        private ContentPaths contentPaths;

        private boolean deleteOnline = false;

        private DeleteContentListener callback;

        private Builder()
        {
        }

        public Builder contentPaths( ContentPaths contentPaths )
        {
            this.contentPaths = contentPaths;
            return this;
        }

        public Builder deleteOnline( boolean deleteOnline )
        {
            this.deleteOnline = deleteOnline;
            return this;
        }

        public Builder callback( DeleteContentListener callback )
        {
            this.callback = callback;
            return this;
        }

        public DeleteContentsParams build()
        {
            return new DeleteContentsParams( this );
        }
    }
}
