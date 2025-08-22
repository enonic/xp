package com.enonic.xp.content;

import java.util.Objects;

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

    public DeleteContentListener getDeleteContentListener()
    {
        return deleteContentListener;
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

        public Builder deleteContentListener( final DeleteContentListener deleteContentListener )
        {
            this.deleteContentListener = deleteContentListener;
            return this;
        }

        public DeleteContentParams build()
        {
            Objects.requireNonNull( this.contentPath, "contentPath is required" );
            return new DeleteContentParams( this );
        }
    }
}
