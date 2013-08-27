package com.enonic.wem.core.index.elastic.result;

import java.util.List;

import com.enonic.wem.api.facet.Facet;
import com.enonic.wem.api.facet.TermsFacet;

public class TermFacetFactory
{
    static Facet create( final String facetName, final org.elasticsearch.search.facet.terms.TermsFacet facet )
    {
        TermsFacet termsFacet = new TermsFacet();
        termsFacet.setName( facetName );
        termsFacet.setTotal( facet.getTotalCount() );
        termsFacet.setMissing( facet.getMissingCount() );
        termsFacet.setOther( facet.getOtherCount() );

        final List<? extends org.elasticsearch.search.facet.terms.TermsFacet.Entry> entries = facet.getEntries();
        for ( org.elasticsearch.search.facet.terms.TermsFacet.Entry entry : entries )
        {
            termsFacet.addResult( entry.getTerm().toString(), entry.getCount() );
        }

        return termsFacet;
    }

}