package com.enonic.wem.core.elasticsearch.result;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

public class SearchResultEntries
    implements Iterable<SearchResultEntry>
{
    public final Set<SearchResultEntry> hits;

    public final long totalHits;

    private final float maxScore;

    public SearchResultEntries( final Builder builder )
    {
        this.hits = ImmutableSet.copyOf( builder.hits );
        this.totalHits = builder.totalHits;
        this.maxScore = builder.maxScore;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public long getSize()
    {
        return hits.size();
    }

    public float getMaxScore()
    {
        return maxScore;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public SearchResultEntry getFirstHit()
    {
        return hits.iterator().next();
    }

    public Set<SearchResultField> getFields( final String fieldName )
    {
        final Set<SearchResultField> searchResultFields = Sets.newLinkedHashSet();

        for ( final SearchResultEntry hit : hits )
        {
            searchResultFields.add( hit.getField( fieldName ) );
        }

        return searchResultFields;
    }

    @Override
    public Iterator<SearchResultEntry> iterator()
    {
        return hits.iterator();
    }

    public static class Builder
    {
        private Set<SearchResultEntry> hits = Sets.newLinkedHashSet();

        private long totalHits = 0;

        private float maxScore = 0;

        public Builder add( final SearchResultEntry entry )
        {
            hits.add( entry );
            return this;
        }

        public Builder totalHits( final long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public Builder maxScore( final float maxScore )
        {
            this.maxScore = maxScore;
            return this;
        }

        public SearchResultEntries build()
        {
            return new SearchResultEntries( this );
        }

    }
}
