package com.enonic.xp.content;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
@NullMarked
public final class GetContentVersionsResult
{
    private final ContentVersions contentVersions;

    private final long totalHits;

    private final @Nullable String cursor;

    private GetContentVersionsResult( Builder builder )
    {
        contentVersions = Objects.requireNonNull( builder.contentVersions );
        totalHits = builder.totalHits;
        cursor = builder.cursor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentVersions getContentVersions()
    {
        return contentVersions;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public @Nullable String getCursor()
    {
        return cursor;
    }

    public static final class Builder
    {
        @Nullable
        private ContentVersions contentVersions;

        private long totalHits;

        @Nullable
        private String cursor;

        private Builder()
        {
        }

        public Builder contentVersions( ContentVersions contentVersions )
        {
            this.contentVersions = contentVersions;
            return this;
        }

        public Builder totalHits( long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public Builder cursor( final @Nullable String cursor )
        {
            this.cursor = cursor;
            return this;
        }

        public GetContentVersionsResult build()
        {
            return new GetContentVersionsResult( this );
        }
    }
}
