package com.enonic.wem.api.entity;

public class FindEntityVersionsResult
{
    final EntityVersions entityVersions;

    private final int from;

    private final int size;

    private final long totalHits;

    private final long hits;

    private FindEntityVersionsResult( Builder builder )
    {
        entityVersions = builder.entityVersions;
        from = builder.from;
        size = builder.size;
        totalHits = builder.totalHits;
        hits = builder.hits;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public EntityVersions getEntityVersions()
    {
        return entityVersions;
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
        private EntityVersions entityVersions;

        private int from;

        private int size;

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder entityVersions( EntityVersions entityVersions )
        {
            this.entityVersions = entityVersions;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder to( int to )
        {
            this.size = to;
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

        public FindEntityVersionsResult build()
        {
            return new FindEntityVersionsResult( this );
        }
    }
}
