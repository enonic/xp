package com.enonic.wem.core.index.facet;

import java.util.Iterator;

import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.query.QueryFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;

import com.enonic.wem.api.facet.Facets;


public class FacetsFactory
{
    private TermsFacetFactory termsFacetFactory = new TermsFacetFactory();

    private QueryFacetFactory queryFacetFactory = new QueryFacetFactory();

    public Facets create( final org.elasticsearch.search.facet.Facets searchResponseFacets )
    {
        if ( searchResponseFacets == null )
        {
            return null;
        }

        final Facets facets = new Facets();

        final Iterator<Facet> facetsIterator = searchResponseFacets.iterator();

        while ( facetsIterator.hasNext() )
        {
            final Facet facet = facetsIterator.next();

            if ( facet instanceof TermsFacet )
            {
                facets.addFacet( termsFacetFactory.create( (TermsFacet) facet ) );

            }
            else if ( facet instanceof QueryFacet )
            {
                facets.addFacet( queryFacetFactory.create( (QueryFacet) facet ) );
            }
            else
            {
                throw new IllegalArgumentException( "Facet of type: '" + facet.getClass().getName() + " not supported" );
            }
        }

        return facets;
    }

}
