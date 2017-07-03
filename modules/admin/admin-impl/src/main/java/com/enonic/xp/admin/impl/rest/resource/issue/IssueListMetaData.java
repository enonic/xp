package com.enonic.xp.admin.impl.rest.resource.issue;

public final class IssueListMetaData
{
    private final long totalHits;

    private final long hits;

    private IssueListMetaData( final Builder builder )
    {
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
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

        public IssueListMetaData build()
        {
            return new IssueListMetaData( this );
        }
    }
}
