package com.enonic.wem.api.facet;

public abstract class AbstractFacet
    implements Facet
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
