package com.enonic.wem.api.query;

import java.util.Map;

import com.google.common.collect.Maps;

public class TermsFacetResultSet
    extends AbstractFacetResultSet
    implements FacetResultSet
{
    private Long total;

    private Long missing;

    private Long other;

    private Map<String, Integer> results = Maps.newLinkedHashMap();

    public Map<String, Integer> getResults()
    {
        return results;
    }

    public void addResult( String term, Integer count )
    {
        results.put( term, count );
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
