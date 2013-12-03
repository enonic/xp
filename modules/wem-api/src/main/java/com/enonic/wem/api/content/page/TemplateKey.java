package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.module.ModuleKey;

import static com.google.common.base.Preconditions.checkNotNull;


public abstract class TemplateKey<NAME extends TemplateName>
{
    protected static final String SEPARATOR = "|";

    private final NAME name;

    private final SiteTemplateKey siteTemplate;

    private final ModuleKey module;

    private final String refString;

    protected TemplateKey( final SiteTemplateKey siteTemplate, final ModuleKey module, final NAME name )
    {
        checkNotNull( name, "Template name cannot be null" );
        checkNotNull( siteTemplate, "SiteTemplate name cannot be null" );
        checkNotNull( module, "ModuleKey name cannot be null" );
        this.name = name;
        this.siteTemplate = siteTemplate;
        this.module = module;
        this.refString = siteTemplate.toString() + SEPARATOR + module.toString() + SEPARATOR + name.toString();
    }

    public NAME getTemplateName()
    {
        return name;
    }

    public SiteTemplateKey getSiteTemplateKey()
    {
        return siteTemplate;
    }

    public ModuleKey getModuleKey()
    {
        return module;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final TemplateKey that = (TemplateKey) o;
        if ( !refString.equals( that.refString ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }
}
