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

    private FindContentIdsByQueryResult( final Builder builder )
    {
        this.contentIds = Objects.requireNonNull( builder.contentIds );
        this.totalHits = builder.totalHits;
        this.aggregations = builder.aggregations;
        this.highlight = builder.highlight != null ? ImmutableMap.copyOf( builder.highlight ) : null;
        this.sort = builder.sort != null ? ImmutableMap.copyOf( builder.sort ) : null;
        this.score = builder.score != null ? ImmutableMap.copyOf( builder.score ) : null;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Aggregations getAggregations()
    {
        return aggregations;
    }

    public Map<ContentId, HighlightedProperties> getHighlight()
    {
        return highlight;
    }

    public Map<ContentId, SortValuesProperty> getSort()
    {
        return sort;
    }

    public Map<ContentId, Float> getScore()
    {
        return score;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Aggregations aggregations;

        private Map<ContentId, HighlightedProperties> highlight;

        private Map<ContentId, SortValuesProperty> sort;

        private Map<ContentId, Float> score;

        private long totalHits;

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
