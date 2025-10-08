package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class FindContentPathsByQueryResult
{
    private final ContentPaths contentPaths;

    private final long totalHits;

    private FindContentPathsByQueryResult( final Builder builder )
    {
        this.contentPaths = builder.contentPaths;
        this.totalHits = builder.totalHits;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentPaths getContentPaths()
    {
        return contentPaths;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public static final class Builder
    {
        private ContentPaths contentPaths = ContentPaths.empty();

        private long totalHits = 0;

        private Builder()
        {
        }

        public Builder contentPaths( ContentPaths contentPaths )
        {
            this.contentPaths = contentPaths;
            return this;
        }

        public Builder totalHits( long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public FindContentPathsByQueryResult build()
        {
            return new FindContentPathsByQueryResult( this );
        }
    }
}
