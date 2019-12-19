package com.enonic.xp.repo.impl.search.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class SearchHits
    implements Iterable<SearchHit>
{
    private final List<SearchHit> hits;

    private SearchHits( final Builder builder )
    {
        this.hits = builder.hits;
    }

    public long getSize()
    {
        return hits.size();
    }

    public SearchHit getFirst()
    {
        return this.hits.get( 0 );
    }

    public Stream<SearchHit> stream()
    {
        return this.hits.stream();
    }

    @Override
    public Iterator<SearchHit> iterator()
    {
        return this.hits.iterator();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<SearchHit> hits = new ArrayList<>();


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

        public Builder addAll( final Collection<SearchHit> entries )
        {
            this.hits.addAll( entries );
            return this;
        }


        public SearchHits build()
        {
            return new SearchHits( this );
        }

    }
}
