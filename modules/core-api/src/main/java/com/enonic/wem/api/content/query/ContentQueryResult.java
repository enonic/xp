package com.enonic.wem.api.content.query;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.aggregation.Aggregations;
import com.enonic.wem.api.content.ContentId;

public class ContentQueryResult
{
    private final long totalSize;

    private final ImmutableSet<ContentQueryHit> contentQueryHits;

    private final Aggregations aggregations;

    private ContentQueryResult( final Builder builder )
    {
        this.totalSize = builder.totalSize;
        this.contentQueryHits = ImmutableSet.copyOf( builder.contentQueryHits );
        this.aggregations = builder.aggregations;
    }

    public long getTotalSize()
    {
        return totalSize;
    }

    public Set<ContentId> getContentIds()
    {
        final LinkedHashSet<ContentId> contentIds = Sets.newLinkedHashSet();

        for ( ContentQueryHit hit : contentQueryHits )
        {
            contentIds.add( hit.getContentId() );
        }

        return contentIds;
    }

    public Set<ContentQueryHit> getContentQueryHits()
    {
        return contentQueryHits;
    }

    public Aggregations getAggregations()
    {
        return aggregations;
    }

    public static Builder newResult( long totalSize )
    {
        return new Builder( totalSize );
    }

    public static class Builder
    {
        private long totalSize;

        private Set<ContentQueryHit> contentQueryHits = Sets.newLinkedHashSet();

        private Aggregations aggregations;

        public Builder( final long totalSize )
        {
            this.totalSize = totalSize;
        }

        public Builder addContentHit( final ContentId contentId, final float score )
        {
            this.contentQueryHits.add( new ContentQueryHit( score, contentId ) );
            return this;
        }

        public Builder setAggregations( final Aggregations aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public ContentQueryResult build()
        {
            return new ContentQueryResult( this );
        }
    }


}
