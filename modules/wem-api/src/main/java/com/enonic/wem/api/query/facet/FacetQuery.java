package com.enonic.wem.api.query.facet;

public abstract class FacetQuery
{
    private final String name;

    protected FacetQuery( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public static TermsFacetQuery.Builder newTermsFacetQuery( final String name )
    {
        return new TermsFacetQuery.Builder( name );
    }

    public static QueryFacetQuery.Builder newQueryFacetQuery( final String name )
    {
        return new QueryFacetQuery.Builder( name );
    }


    static class Builder
    {
        String name;

        Builder( final String name )
        {
            this.name = name;
        }
    }

}
