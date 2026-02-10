package com.enonic.xp.content;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
@NullMarked
public final class GetContentVersionsParams
{
    private final ContentId contentId;

    @Nullable
    private final String cursor;

    private final int size;

    private GetContentVersionsParams( final Builder builder )
    {
        contentId = Objects.requireNonNull( builder.contentId, "contentId cannot be null" );
        cursor = builder.cursor;
        size = builder.size;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    @Nullable
    public String getCursor()
    {
        return cursor;
    }

    public int getSize()
    {
        return size;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private @Nullable ContentId contentId;

        private @Nullable String cursor;

        private int size = 10;

        private Builder()
        {
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder cursor( final @Nullable String cursor )
        {
            this.cursor = cursor;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public GetContentVersionsParams build()
        {
            return new GetContentVersionsParams( this );
        }
    }
}
