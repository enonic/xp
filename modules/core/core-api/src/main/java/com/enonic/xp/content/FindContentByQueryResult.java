package com.enonic.xp.content;

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.highlight.HighlightedFields;
import com.enonic.xp.node.NodeId;

@Beta
public final class FindContentByQueryResult
{
    private final Aggregations aggregations;

    private Contents contents;

    private final ImmutableMap<ContentId, HighlightedFields> highlight;

    private long totalHits;

    private long hits;

    private FindContentByQueryResult( final Builder builder )
    {
        this.contents = builder.contents;
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.aggregations = builder.aggregations;
        this.highlight = ImmutableMap.copyOf( builder.highlight );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Contents getContents()
    {
        return contents;
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

    public static final class Builder
    {
        private Contents contents;

        private Aggregations aggregations;

        private Map<ContentId, HighlightedFields> highlight = Maps.newHashMap();

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

        public Builder hits( long hits )
        {
            this.hits = hits;
            return this;
        }

        public Builder highlight( final Map<ContentId, HighlightedFields> highlight)
        {
            this.highlight = highlight;
            return this;
        }

        public FindContentByQueryResult build()
        {
            return new FindContentByQueryResult( this );
        }
    }
}
