package com.enonic.wem.api.facet;

public class QueryFacet
    extends AbstractFacet
{
    private Long count;

    public QueryFacet( final Long count )
    {
        this.count = count;
    }

    public Long getCount()
    {
        return count;
    }
}


