package com.enonic.wem.core.index.facet;

import java.util.List;

import com.enonic.wem.api.facet.TermsFacet;

public class TermsFacetFactory
{

    public TermsFacet create( final org.elasticsearch.search.facet.terms.TermsFacet searchResultFacet )
    {
        final TermsFacet.Builder builder = TermsFacet.newTermsFacet( searchResultFacet.getName() ).
            missing( searchResultFacet.getMissingCount() ).
            other( searchResultFacet.getOtherCount() ).
            total( searchResultFacet.getTotalCount() );

        final List<? extends org.elasticsearch.search.facet.terms.TermsFacet.Entry> entries = searchResultFacet.getEntries();
        for ( org.elasticsearch.search.facet.terms.TermsFacet.Entry entry : entries )
        {
            builder.addEntry( entry.getTerm().toString(), entry.getCount() );
        }

        return builder.build();
    }

}
