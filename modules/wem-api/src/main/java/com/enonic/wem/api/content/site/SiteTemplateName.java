package com.enonic.wem.api.content.site;


import static com.google.common.base.Preconditions.checkNotNull;

public final class SiteTemplateName
{
    private final String name;

    public SiteTemplateName( final String name )
    {
        checkNotNull( name, "SiteTemplate name cannot be null" );
        this.name = name;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof SiteTemplateName ) && ( (SiteTemplateName) o ).name.equals( this.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }
}
