package com.enonic.xp.repo.impl.search.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchHits
    implements Iterable<SearchHit>
{
    private final List<SearchHit> hits;

    private final long totalHits;

    private final float maxScore;

    private SearchHits( final Builder builder )
    {
        this.hits = builder.hits;
        this.totalHits = builder.totalHits;
        this.maxScore = builder.maxScore;
    }

    public static Builder create( final Long totalHits )
    {
        return new Builder( totalHits );
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

    public SearchHit getFirstHit()
    {
        return this.hits.get( 0 );
    }

    @Override
    public Iterator<SearchHit> iterator()
    {
        return this.hits.iterator();
    }

    public static class Builder
    {
        private List<SearchHit> hits;

        private long totalHits = 0L;

        private float maxScore = 0;

        public Builder( final Long totalHits )
        {
            this.totalHits = totalHits;
            this.hits = new ArrayList<>( totalHits.intValue() );
        }

        public Builder add( final SearchHit entry )
        {
            hits.add( entry );
            return this;
        }

        public Builder addAll( final SearchHits entries )
        {
            this.hits.addAll( entries.hits );
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

        public SearchHits build()
        {
            return new SearchHits( this );
        }

    }
}
