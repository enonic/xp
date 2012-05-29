package com.enonic.wem.core.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Facets
    implements Iterable<Facet>
{
    private final List<Facet> facets;

    public Facets()
    {
        this.facets = new ArrayList<Facet>();
    }

    public void addFacet( Facet facet )
    {
        this.facets.add( facet );
    }

    public int getCount()
    {
        return this.facets.size();
    }

    public Iterator<Facet> iterator()
    {
        return this.facets.iterator();
    }

    public void consolidate()
    {
        for ( Facet facet : facets )
        {
            facet.consolidate();
        }
    }
}
