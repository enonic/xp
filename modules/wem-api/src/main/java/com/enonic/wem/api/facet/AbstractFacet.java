package com.enonic.wem.api.facet;

public abstract class AbstractFacet
    implements Facet
{
    protected String name;

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    protected AbstractFacet( final String name )
    {
        this.name = name;
    }

    protected AbstractFacet()
    {
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
