package com.enonic.wem.core.index.entity;

import java.util.Set;

import org.elasticsearch.search.SearchHit;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class EntitySearchResult
{
    private final ImmutableSet<EntitySearchResultEntry> entries;

    private final long totalHits;

    private final long hits;

    private EntitySearchResult( final Builder builder )
    {
        this.entries = ImmutableSet.copyOf( builder.entries );
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
    }

    public static Builder newResult()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<EntitySearchResultEntry> entries = Sets.newHashSet();

        private long totalHits;

        private long hits;

        public EntitySearchResult build()
        {
            return new EntitySearchResult( this );
        }

        public Builder totalHits( final long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public Builder hits( final long hits )
        {
            this.hits = hits;
            return this;
        }

        public Builder addEntries( final SearchHit[] hits )
        {
            for ( final SearchHit hit : hits )
            {
                entries.add( new EntitySearchResultEntry( hit.score(), hit.id() ) );
            }

            return this;
        }

    }

}
