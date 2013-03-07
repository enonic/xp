package com.enonic.wem.api.query;

public abstract class AbstractFacetResultSet
    implements FacetResultSet
{

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }
}
