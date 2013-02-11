package com.enonic.wem.core.search.facet;

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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Facets facets1 = (Facets) o;

        if ( facets != null ? !facets.equals( facets1.facets ) : facets1.facets != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return facets != null ? facets.hashCode() : 0;
    }

    public void consolidate()
    {
        for ( Facet facet : facets )
        {
            facet.consolidate();
        }
    }
}
