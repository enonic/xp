package com.enonic.wem.api.query;

import java.util.Set;

import com.google.common.collect.Sets;

public class RangeFacetResultSet
    extends AbstractFacetResultSet
    implements FacetResultSet
{
    Set<RangeFacetResultEntry> resultEntries = Sets.newLinkedHashSet();

    public void addResult( RangeFacetResultEntry rangeFacetResultEntry )
    {
        resultEntries.add( rangeFacetResultEntry );
    }

    public Set<RangeFacetResultEntry> getResultEntries()
    {
        return resultEntries;
    }
}
