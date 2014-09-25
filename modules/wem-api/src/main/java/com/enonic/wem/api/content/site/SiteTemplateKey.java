package com.enonic.wem.api.content.site;

import static com.google.common.base.Preconditions.checkNotNull;


public final class SiteTemplateKey
{
    private final SiteTemplateName name;

    private SiteTemplateKey( final SiteTemplateName name )
    {
        checkNotNull( name );
        this.name = name;
    }

    public SiteTemplateName getName()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || !( o instanceof SiteTemplateKey ) )
        {
            return false;
        }
        final SiteTemplateKey that = (SiteTemplateKey) o;
        return name.equals( that.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return name.toString();
    }

    public static SiteTemplateKey from( final String siteTemplateName )
    {
        return new SiteTemplateKey( new SiteTemplateName( siteTemplateName ) );
    }
}
