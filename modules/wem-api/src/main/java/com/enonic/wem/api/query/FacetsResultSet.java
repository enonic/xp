package com.enonic.wem.api.query;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

public class FacetsResultSet
    implements Iterable<FacetResultSet>
{
    Set<FacetResultSet> facetResultSets = Sets.newLinkedHashSet();

    @Override
    public Iterator<FacetResultSet> iterator()
    {
        return facetResultSets.iterator();
    }

    public void addFacetResultSet( FacetResultSet facetResultSet )
    {
        if ( facetResultSets == null )
        {
            this.facetResultSets = Sets.newLinkedHashSet();
        }

        this.facetResultSets.add( facetResultSet );
    }

}

