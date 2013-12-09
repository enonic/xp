package com.enonic.wem.core.index.elastic.result;

import java.util.List;

import com.enonic.wem.api.facet.Facet;
import com.enonic.wem.api.facet.TermsFacet;

@Deprecated
public class OldTermFacetFactory
{
    static Facet create( final org.elasticsearch.search.facet.terms.TermsFacet facet )
    {
        TermsFacet.Builder builder = TermsFacet.newTermsFacet( facet.getName() ).
            total( facet.getTotalCount() ).
            missing( facet.getMissingCount() ).
            other( facet.getOtherCount() );

        final List<? extends org.elasticsearch.search.facet.terms.TermsFacet.Entry> entries = facet.getEntries();
        for ( org.elasticsearch.search.facet.terms.TermsFacet.Entry entry : entries )
        {
            builder.addEntry( entry.getTerm().toString(), entry.getCount() );
        }

        return builder.build();
    }

}