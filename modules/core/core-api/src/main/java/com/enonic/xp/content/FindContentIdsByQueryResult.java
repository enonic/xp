package com.enonic.xp.content;

import java.util.Map;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.index.FieldValues;
import com.enonic.xp.sortvalues.SortValuesProperty;

import static java.util.Objects.requireNonNull;


public final class FindContentIdsByQueryResult
{
    private final Aggregations aggregations;

    private final ContentIds contentIds;

    private final ImmutableMap<ContentId, HighlightedProperties> highlight;

    private final ImmutableMap<ContentId, SortValuesProperty> sort;

    private final ImmutableMap<ContentId, Float> score;

    private final ImmutableMap<ContentId, FieldValues> fields;

    private final long totalHits;

    private FindContentIdsByQueryResult( final Builder builder )
    {
        this.contentIds = requireNonNull( builder.contentIds );
        this.totalHits = builder.totalHits;
        this.aggregations = builder.aggregations;
        this.highlight = builder.highlight != null ? ImmutableMap.copyOf( builder.highlight ) : null;
        this.sort = builder.sort != null ? ImmutableMap.copyOf( builder.sort ) : null;
        this.score = builder.score != null ? ImmutableMap.copyOf( builder.score ) : null;
        this.fields = ImmutableMap.copyOf( builder.fields );
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

    /**
     * The fields requested through {@link ContentQuery.Builder#returnFields(com.enonic.xp.index.IndexPath...)}, keyed by content id.
     * A hit that holds no value for any of them has no entry.
     *
     * @since 8.1.0
     */
    public Map<ContentId, FieldValues> getFields()
    {
        return fields;
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

        private Map<ContentId, FieldValues> fields = Map.of();

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

        public Builder fields( final Map<ContentId, FieldValues> fields )
        {
            this.fields = fields;
            return this;
        }

        public FindContentIdsByQueryResult build()
        {
            return new FindContentIdsByQueryResult( this );
        }
    }
}
