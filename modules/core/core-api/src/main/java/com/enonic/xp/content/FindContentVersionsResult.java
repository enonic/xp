package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class FindContentVersionsResult
{
    private final ContentVersions contentVersions;

    private final long totalHits;

    private FindContentVersionsResult( Builder builder )
    {
        contentVersions = builder.contentVersions;
        totalHits = builder.totalHits;
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

    public static final class Builder
    {
        private ContentVersions contentVersions;

        private long totalHits;

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

        public FindContentVersionsResult build()
        {
            return new FindContentVersionsResult( this );
        }
    }
}
