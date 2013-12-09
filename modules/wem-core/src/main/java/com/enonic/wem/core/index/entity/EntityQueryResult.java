package com.enonic.wem.core.index.entity;

import java.util.Set;

import org.elasticsearch.search.SearchHit;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.facet.Facets;

public class EntityQueryResult
{
    private final ImmutableSet<EntitySearchResultEntry> entries;

    private final long totalHits;

    private final long hits;

    private final float maxScore;

    private final Facets facets;

    private EntityQueryResult( final Builder builder )
    {
        this.entries = ImmutableSet.copyOf( builder.entries );
        this.totalHits = builder.totalHits;
        this.hits = builder.hits;
        this.maxScore = builder.maxScore;
        this.facets = builder.facets;
    }

    public static Builder newResult()
    {
        return new Builder();
    }

    public ImmutableSet<EntitySearchResultEntry> getEntries()
    {
        return entries;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public long getHits()
    {
        return hits;
    }

    public float getMaxScore()
    {
        return maxScore;
    }

    public Facets getFacets()
    {
        return facets;
    }

    public static class Builder
    {
        private Set<EntitySearchResultEntry> entries = Sets.newHashSet();

        private long totalHits;

        private long hits;

        private float maxScore;

        private Facets facets;

        public EntityQueryResult build()
        {
            return new EntityQueryResult( this );
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

        public Builder maxScore( final float maxScore )
        {
            this.maxScore = maxScore;
            return this;
        }

        public Builder facets( final Facets facets )
        {
            this.facets = facets;
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
