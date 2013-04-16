package com.enonic.wem.api.query;

public abstract class AbstractFacetResultSet
    implements FacetResultSet
{

    private String name;

    private String displayName;

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }
}
