package com.enonic.wem.api.content.page.part;


import java.util.Objects;

import com.enonic.wem.api.content.site.SiteTemplateKey;

public class PartTemplateKey
{
    private final SiteTemplateKey siteTemplateKey;

    private final PartTemplateName templateName;

    public PartTemplateKey( final SiteTemplateKey siteTemplateKey, final PartTemplateName templateName )
    {
        this.siteTemplateKey = siteTemplateKey;
        this.templateName = templateName;
    }

    public SiteTemplateKey getSiteTemplateKey()
    {
        return siteTemplateKey;
    }

    public PartTemplateName getTemplateName()
    {
        return templateName;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PartTemplateKey ) )
        {
            return false;
        }

        final PartTemplateKey that = (PartTemplateKey) o;

        return Objects.equals( this.siteTemplateKey, that.siteTemplateKey ) && Objects.equals( this.templateName, that.templateName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.siteTemplateKey, this.templateName );
    }
}
