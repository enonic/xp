package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class DeleteContentParams
{
    private final ContentPath contentPath;

    private final DeleteContentListener deleteContentListener;

    private DeleteContentParams( Builder builder )
    {
        contentPath = builder.contentPath;
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

    @Deprecated
    public boolean isDeleteOnline()
    {
        return true;
    }

    public DeleteContentListener getDeleteContentListener()
    {
        return deleteContentListener;
    }

    @Deprecated
    public void validate()
    {
    }

    @Override
    public boolean equals( final Object o )
    {
        return super.equals( o );
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    public static final class Builder
    {
        private ContentPath contentPath;

        private DeleteContentListener deleteContentListener;

        private Builder()
        {
        }

        public Builder contentPath( ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        @Deprecated
        public Builder deleteOnline( boolean deleteOnline )
        {
            return this;
        }

        public Builder deleteContentListener( final DeleteContentListener deleteContentListener )
        {
            this.deleteContentListener = deleteContentListener;
            return this;
        }

        public DeleteContentParams build()
        {
            Preconditions.checkNotNull( this.contentPath, "ContentPath cannot be null" );
            Preconditions.checkArgument( this.contentPath.isAbsolute(), "ContentPath must be absolute: " + this.contentPath );
            return new DeleteContentParams( this );
        }
    }
}
