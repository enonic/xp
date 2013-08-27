package com.enonic.wem.core.index.elastic.result;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.datehistogram.DateHistogramFacet;
import org.elasticsearch.search.facet.query.QueryFacet;
import org.elasticsearch.search.facet.range.RangeFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;

import com.enonic.wem.api.facet.Facets;

public class FacetFactory
{

    public static Facets create( final SearchResponse searchResponse )
    {
        org.elasticsearch.search.facet.Facets searchResponseFacets = searchResponse.getFacets();

        if ( searchResponseFacets == null )
        {
            return null;
        }

        final Facets facets = new Facets();

        final Map<String, Facet> facetsMap = searchResponseFacets.getFacets();

        for ( String facetName : facetsMap.keySet() )
        {
            final Facet facet = facetsMap.get( facetName );

            if ( facet instanceof TermsFacet )
            {
                facets.addFacet( TermFacetFactory.create( facetName, (TermsFacet) facet ) );
            }

            else if ( facet instanceof DateHistogramFacet )
            {
                facets.addFacet( DateHistogramFacetFactory.create( facetName, (DateHistogramFacet) facet ) );
            }

            else if ( facet instanceof RangeFacet )
            {
                facets.addFacet( RangeFacetFactory.create( facetName, (RangeFacet) facet ) );
            }
            else if ( facet instanceof QueryFacet )
            {
                facets.addFacet( QueryFacetFactory.create( facetName, (QueryFacet) facet ) );
            }
            /*
                Other facets to come
            */
        }

        return facets;
    }
}

