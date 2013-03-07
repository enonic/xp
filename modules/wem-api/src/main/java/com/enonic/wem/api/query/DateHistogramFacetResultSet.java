package com.enonic.wem.api.query;

import java.util.Set;

import com.google.common.collect.Sets;

public class DateHistogramFacetResultSet
    extends AbstractFacetResultSet
    implements FacetResultSet
{
    Set<DateHistogramFacetResultEntry> resultEntries = Sets.newLinkedHashSet();


    public void addResult( DateHistogramFacetResultEntry result )
    {
        resultEntries.add( result );
    }

    public Set<DateHistogramFacetResultEntry> getResultEntries()
    {
        return resultEntries;
    }

}
