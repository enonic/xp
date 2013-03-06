package com.enonic.wem.core.index.content;

import java.util.Set;

import com.google.common.collect.Sets;

public class ContentSearchResults
{
    private final int from;

    private final int total;

    private final Set<ContentSearchHit> hits;

    public ContentSearchResults( final int total, final int from )
    {
        this.total = total;
        this.from = from;
        this.hits = Sets.newHashSet();
    }

    public void add( final ContentSearchHit hit )
    {
        this.hits.add( hit );
    }

    public Set<ContentSearchHit> getHits()
    {
        return hits;
    }

    public int getTotal()
    {
        return total;
    }
}
