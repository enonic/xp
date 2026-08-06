package com.enonic.xp.content;

import java.util.Objects;

/**
 * One content in a {@link ListContentsByParentResult}: its id and path, without reading the content itself.
 *
 * @since 8.1.0
 */
public final class ContentListEntry
{
    private final ContentId id;

    private final ContentPath path;

    private ContentListEntry( final Builder builder )
    {
        this.id = Objects.requireNonNull( builder.id );
        this.path = Objects.requireNonNull( builder.path );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getId()
    {
        return id;
    }

    public ContentPath getPath()
    {
        return path;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof ContentListEntry that && id.equals( that.id ) && path.equals( that.path );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, path );
    }

    @Override
    public String toString()
    {
        return path + " [" + id + "]";
    }

    public static final class Builder
    {
        private ContentId id;

        private ContentPath path;

        private Builder()
        {
        }

        public Builder id( final ContentId id )
        {
            this.id = id;
            return this;
        }

        public Builder path( final ContentPath path )
        {
            this.path = path;
            return this;
        }

        public ContentListEntry build()
        {
            return new ContentListEntry( this );
        }
    }
}
