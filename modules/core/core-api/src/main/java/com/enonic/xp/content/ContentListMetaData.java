package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class ContentListMetaData
{
    private final long totalHits;

    private final long hits;

    private ContentListMetaData( Builder builder )
    {
        totalHits = builder.totalHits;
        hits = builder.hits;
    }


    public long getTotalHits()
    {
        return totalHits;
    }

    public long getHits()
    {
        return hits;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private long totalHits;

        private long hits;

        private Builder()
        {
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

        public ContentListMetaData build()
        {
            return new ContentListMetaData( this );
        }
    }
}
