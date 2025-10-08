package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class FindContentIdsByParentResult
{
    private final ContentIds contentIds;

    private final long totalHits;

    private FindContentIdsByParentResult( Builder builder )
    {
        contentIds = builder.contentIds;
        totalHits = builder.totalHits;
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

    public static final class Builder
    {
        private ContentIds contentIds;

        private long totalHits;

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

        public FindContentIdsByParentResult build()
        {
            return new FindContentIdsByParentResult( this );
        }
    }
}
