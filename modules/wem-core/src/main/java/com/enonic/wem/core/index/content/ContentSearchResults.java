package com.enonic.wem.core.index.content;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.facet.Facets;

public class ContentSearchResults
{
    private final int from;

    private final int total;

    private final Set<ContentSearchHit> hits;

    private Facets facets;

    public ContentSearchResults( final int total, final int from )
    {
        this.total = total;
        this.from = from;
        this.hits = Sets.newLinkedHashSet();
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

    public void setFacets( final Facets facets )
    {
        this.facets = facets;
    }

    public Facets getFacets()
    {
        return facets;
    }
}
