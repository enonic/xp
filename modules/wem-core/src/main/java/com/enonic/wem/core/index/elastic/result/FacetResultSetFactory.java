package com.enonic.wem.core.index.elastic.result;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.datehistogram.DateHistogramFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;

import com.enonic.wem.api.query.FacetsResultSet;

public class FacetResultSetFactory
{

    // private final RangeFacetResultSetCreator rangeFacetResultSetCreator = new RangeFacetResultSetCreator();

    //  private final HistogramFacetResultSetCreator histogramFacetResultSetCreator = new HistogramFacetResultSetCreator();

    //  private final TermsStatsFacetResultSetCreator termsStatsFacetResultSetCreator = new TermsStatsFacetResultSetCreator();

    public static FacetsResultSet create( final SearchResponse searchResponse )
    {
        Facets facets = searchResponse.getFacets();

        if ( facets == null )
        {
            return null;
        }

        final FacetsResultSet facetsResultSet = new FacetsResultSet();

        final Map<String, Facet> facetsMap = facets.getFacets();

        for ( String facetName : facetsMap.keySet() )
        {
            final Facet facet = facetsMap.get( facetName );

            if ( facet instanceof TermsFacet )
            {
                facetsResultSet.addFacetResultSet( TermFacetResultSetFactory.create( facetName, (TermsFacet) facet ) );
            }

            else if ( facet instanceof DateHistogramFacet )
            {
                facetsResultSet.addFacetResultSet( DateHistogramFacetResultSetFactory.create( facetName, (DateHistogramFacet) facet ) );
            }
             /*
                else if ( facet instanceof RangeFacet )
                {
                    facetsResultSet.addFacetResultSet( rangeFacetResultSetCreator.create( facetName, (RangeFacet) facet ) );
                }
                else if ( facet instanceof HistogramFacet )
                {
                    facetsResultSet.addFacetResultSet( histogramFacetResultSetCreator.create( facetName, (HistogramFacet) facet ) );
                }
                else if ( facet instanceof TermsStatsFacet )
                {
                    facetsResultSet.addFacetResultSet( termsStatsFacetResultSetCreator.create( facetName, (TermsStatsFacet) facet ) );
                }
            */
        }

        return facetsResultSet;
    }
}

