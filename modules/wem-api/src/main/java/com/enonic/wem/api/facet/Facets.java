package com.enonic.wem.api.facet;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

public class Facets
    implements Iterable<Facet>
{
    Set<Facet> facets = Sets.newLinkedHashSet();

    @Override
    public Iterator<Facet> iterator()
    {
        return facets.iterator();
    }

    public void addFacet( Facet facet )
    {
        if ( facets == null )
        {
            this.facets = Sets.newLinkedHashSet();
        }

        this.facets.add( facet );
    }

}

