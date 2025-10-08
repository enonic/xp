package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class FindContentByParentResult
{
    private final Contents contents;

    private final long totalHits;

    private FindContentByParentResult( Builder builder )
    {
        contents = builder.contents;
        totalHits = builder.totalHits;
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

    public static final class Builder
    {
        private Contents contents;

        private long totalHits;

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

        public FindContentByParentResult build()
        {
            return new FindContentByParentResult( this );
        }
    }
}
