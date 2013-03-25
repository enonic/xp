package com.enonic.wem.api.query;

public class QueryFacetResultSet
    extends AbstractFacetResultSet
{
    private Long count;

    public QueryFacetResultSet( final Long count )
    {
        this.count = count;
    }

    public Long getCount()
    {
        return count;
    }
}


