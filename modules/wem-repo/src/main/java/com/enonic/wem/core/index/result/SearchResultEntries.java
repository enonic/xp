package com.enonic.wem.core.index.result;

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
    public final ImmutableMap<String, SearchResultEntry> hits;

    public final long totalHits;

    private final float maxScore;

    public SearchResultEntries( final Builder builder )
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

    public Set<SearchResultField> getFields( final String fieldName )
    {
        final Set<SearchResultField> searchResultFields = Sets.newLinkedHashSet();

        searchResultFields.addAll( hits.values().stream().map( hit -> hit.getField( fieldName ) ).collect( Collectors.toList() ) );

        return searchResultFields;
    }

    @Override
    public Iterator<SearchResultEntry> iterator()
    {
        return this.hits.values().iterator();
    }

    public static class Builder
    {
        private Map<String, SearchResultEntry> hits = Maps.newLinkedHashMap();

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
