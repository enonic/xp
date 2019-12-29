package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class FindContentByParentResult
{
    private final Contents contents;

    private final long totalHits;

    private final long hits;

    private FindContentByParentResult( Builder builder )
    {
        contents = builder.contents;
        totalHits = builder.totalHits;
        hits = builder.hits;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Contents getContents()
    {
        return contents;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public long getHits()
    {
        return hits;
    }

    public static final class Builder
    {
        private Contents contents;

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder contents( Contents contents )
        {
            this.contents = contents;
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

        public FindContentByParentResult build()
        {
            return new FindContentByParentResult( this );
        }
    }
}
