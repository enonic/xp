package com.enonic.xp.content;

import java.util.Map;
import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.highlight.HighlightedFields;

@Beta
public final class FindContentIdsByQueryResult
{
    private final Aggregations aggregations;

    private final ContentIds contentIds;

    private final ImmutableMap<ContentId, HighlightedFields> highlight;

    private final long totalHits;

    private final long hits;

    private FindContentIdsByQueryResult( final Builder builder )
    {
        this.contentIds = builder.contentIds;
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.aggregations = builder.aggregations;
        this.highlight = builder.highlight != null ? ImmutableMap.copyOf( builder.highlight ) : null;
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

    public ImmutableMap<ContentId, HighlightedFields> getHighlight()
    {
        return highlight;
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
            Objects.equals( contentIds, that.contentIds ) && Objects.equals( highlight, that.highlight );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( aggregations, contentIds, totalHits, hits, highlight );
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Aggregations aggregations;

        private Map<ContentId, HighlightedFields> highlight;

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

        public Builder highlight( final Map<ContentId, HighlightedFields> highlight )
        {
            this.highlight = highlight;
            return this;
        }

        public FindContentIdsByQueryResult build()
        {
            return new FindContentIdsByQueryResult( this );
        }
    }
}
