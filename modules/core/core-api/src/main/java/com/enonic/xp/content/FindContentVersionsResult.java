package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class FindContentVersionsResult
{
    private final ContentVersions contentVersions;

    private final long totalHits;

    private final long hits;

    private final int from;

    private final int size;

    private FindContentVersionsResult( Builder builder )
    {
        contentVersions = builder.contentVersions;
        totalHits = builder.totalHits;
        hits = builder.hits;
        from = builder.from;
        size = builder.size;
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

    public long getHits()
    {
        return hits;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public static final class Builder
    {
        private ContentVersions contentVersions;

        private long totalHits;

        private long hits;

        private int from;

        private int size;

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

        public Builder hits( long hits )
        {
            this.hits = hits;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public FindContentVersionsResult build()
        {
            return new FindContentVersionsResult( this );
        }
    }
}
