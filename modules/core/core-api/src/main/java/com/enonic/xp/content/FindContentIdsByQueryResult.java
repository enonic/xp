package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.aggregation.Aggregations;

@Beta
public final class FindContentIdsByQueryResult
{
    private final Aggregations aggregations;

    private final ContentIds contentIds;

    private final long totalHits;

    private final long hits;

    private FindContentIdsByQueryResult( final Builder builder )
    {
        this.contentIds = builder.contentIds;
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.aggregations = builder.aggregations;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static FindContentIdsByQueryResult empty()
    {
        return FindContentIdsByQueryResult.create().contents( ContentIds.empty() ).hits( 0 ).totalHits( 0 ).aggregations(
            Aggregations.empty() ).build();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Aggregations getAggregations()
    {
        return aggregations;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public long getHits()
    {
        return hits;
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final FindContentIdsByQueryResult that = (FindContentIdsByQueryResult) o;
        return totalHits == that.totalHits && hits == that.hits && Objects.equals( aggregations, that.aggregations ) &&
            Objects.equals( contentIds, that.contentIds );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( aggregations, contentIds, totalHits, hits );
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Aggregations aggregations;

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder aggregations( final Aggregations aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public Builder contents( ContentIds contentIds )
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

        public FindContentIdsByQueryResult build()
        {
            return new FindContentIdsByQueryResult( this );
        }
    }
}
