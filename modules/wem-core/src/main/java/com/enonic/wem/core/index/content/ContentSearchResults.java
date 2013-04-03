package com.enonic.wem.core.index.content;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.query.FacetsResultSet;

public class ContentSearchResults
{
    private final int from;

    private final int total;

    private final Set<ContentSearchHit> hits;

    private FacetsResultSet facetsResultSet;

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

    public void setFacetResultSets( final FacetsResultSet facetResults )
    {
        this.facetsResultSet = facetResults;
    }

    public FacetsResultSet getFacetsResultSet()
    {
        return facetsResultSet;
    }
}
