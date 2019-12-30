package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class FindContentIdsByParentResult
{
    private final ContentIds contentIds;

    private final long totalHits;

    private final long hits;

    private FindContentIdsByParentResult( Builder builder )
    {
        contentIds = builder.contentIds;
        totalHits = builder.totalHits;
        hits = builder.hits;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
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
        private ContentIds contentIds;

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
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

        public FindContentIdsByParentResult build()
        {
            return new FindContentIdsByParentResult( this );
        }
    }
}
