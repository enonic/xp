package com.enonic.xp.content;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.sortvalues.SortValuesProperty;

@PublicApi
public final class FindContentIdsByQueryResult
{
    private final Aggregations aggregations;

    private final ContentIds contentIds;

    private final ImmutableMap<ContentId, HighlightedProperties> highlight;

    private final ImmutableMap<ContentId, SortValuesProperty> sort;

    private final ImmutableMap<ContentId, Float> score;

    private final long totalHits;

    private final long hits;

    private FindContentIdsByQueryResult( final Builder builder )
    {
        this.contentIds = builder.contentIds;
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.aggregations = builder.aggregations;
        this.highlight = builder.highlight != null ? ImmutableMap.copyOf( builder.highlight ) : null;
        this.sort = builder.sort != null ? ImmutableMap.copyOf( builder.sort ) : null;
        this.score = builder.score != null ? ImmutableMap.copyOf( builder.score ) : null;
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

    public ImmutableMap<ContentId, HighlightedProperties> getHighlight()
    {
        return highlight;
    }

    public ImmutableMap<ContentId, SortValuesProperty> getSort()
    {
        return sort;
    }

    public ImmutableMap<ContentId, Float> getScore()
    {
        return score;
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
            Objects.equals( contentIds, that.contentIds ) && Objects.equals( highlight, that.highlight ) &&
            Objects.equals( sort, that.sort ) && Objects.equals( score, that.score );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( aggregations, contentIds, totalHits, hits, highlight, sort, score );
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Aggregations aggregations;

        private Map<ContentId, HighlightedProperties> highlight;

        private Map<ContentId, SortValuesProperty> sort;

        private Map<ContentId, Float> score;

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

        public Builder highlight( final Map<ContentId, HighlightedProperties> highlight )
        {
            this.highlight = highlight;
            return this;
        }

        public Builder sort( final Map<ContentId, SortValuesProperty> sort )
        {
            this.sort = sort;
            return this;
        }

        public Builder score( final Map<ContentId, Float> score )
        {
            this.score = score;
            return this;
        }

        public FindContentIdsByQueryResult build()
        {
            return new FindContentIdsByQueryResult( this );
        }
    }
}
