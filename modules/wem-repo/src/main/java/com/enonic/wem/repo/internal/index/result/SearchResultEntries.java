package com.enonic.wem.repo.internal.index.result;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SearchResultEntries
    implements Iterable<SearchResultEntry>
{
    private final ImmutableMap<String, SearchResultEntry> hits;

    private final long totalHits;

    private final float maxScore;

    private SearchResultEntries( final Builder builder )
    {
        this.hits = ImmutableMap.copyOf( builder.hits );
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

    public SearchResultEntry get( final String id )
    {
        return this.hits.get( id );
    }

    public SearchResultEntry getFirstHit()
    {
        return this.hits.values().iterator().next();
    }

    public Set<SearchResultFieldValue> getFields( final String fieldName )
    {
        final Set<SearchResultFieldValue> searchResultFieldValues = Sets.newLinkedHashSet();

        searchResultFieldValues.addAll( hits.values().stream().map( hit -> hit.getField( fieldName ) ).collect( Collectors.toList() ) );

        return searchResultFieldValues;
    }

    @Override
    public Iterator<SearchResultEntry> iterator()
    {
        return this.hits.values().iterator();
    }

    public static class Builder
    {
        private final Map<String, SearchResultEntry> hits = Maps.newLinkedHashMap();

        private long totalHits = 0;

        private float maxScore = 0;

        public Builder add( final SearchResultEntry entry )
        {
            hits.put( entry.getId(), entry );
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
