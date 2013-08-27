package com.enonic.wem.api.facet;

import java.util.Set;

import com.google.common.collect.Sets;

public class RangeFacet
    extends AbstractFacet
    implements Facet
{
    Set<RangeFacetEntry> resultEntries = Sets.newLinkedHashSet();

    public void addResult( RangeFacetEntry rangeFacetEntry )
    {
        resultEntries.add( rangeFacetEntry );
    }

    public Set<RangeFacetEntry> getResultEntries()
    {
        return resultEntries;
    }
}
