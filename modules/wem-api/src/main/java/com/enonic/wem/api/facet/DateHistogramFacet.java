package com.enonic.wem.api.facet;

import java.util.Set;

import com.google.common.collect.Sets;

public class DateHistogramFacet
    extends AbstractFacet
    implements Facet
{
    Set<DateHistogramFacetEntry> resultEntries = Sets.newLinkedHashSet();


    public void addResult( DateHistogramFacetEntry result )
    {
        resultEntries.add( result );
    }

    public Set<DateHistogramFacetEntry> getResultEntries()
    {
        return resultEntries;
    }

}
