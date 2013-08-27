package com.enonic.wem.api.facet;

import java.util.Set;

import com.google.common.collect.Sets;

public class TermsFacet
    extends AbstractFacet
    implements Facet
{
    private Long total;

    private Long missing;

    private Long other;

    private Set<TermsFacetEntry> results = Sets.newLinkedHashSet();

    public Set<TermsFacetEntry> getResults()
    {
        return results;
    }

    public void addResult( String term, Integer count )
    {
        results.add( new TermsFacetEntry( term, count ) );
    }

    public void addResult( String term, String displayName, Integer count )
    {
        results.add( new TermsFacetEntry( term, displayName, count ) );
    }

    public Long getTotal()
    {
        return total;
    }

    public void setTotal( final Long total )
    {
        this.total = total;
    }

    public Long getMissing()
    {
        return missing;
    }

    public void setMissing( final Long missing )
    {
        this.missing = missing;
    }

    public Long getOther()
    {
        return other;
    }

    public void setOther( final Long other )
    {
        this.other = other;
    }


}
