package com.enonic.wem.api.content.page;


import java.util.Objects;

import com.enonic.wem.api.content.site.SiteTemplateKey;

public class PageTemplateKey
{
    private final SiteTemplateKey siteTemplateKey;

    private final PageTemplateName templateName;

    public PageTemplateKey( final SiteTemplateKey siteTemplateKey, final PageTemplateName templateName )
    {
        this.siteTemplateKey = siteTemplateKey;
        this.templateName = templateName;
    }

    public SiteTemplateKey getSiteTemplateKey()
    {
        return siteTemplateKey;
    }

    public PageTemplateName getTemplateName()
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
        if ( !( o instanceof PageTemplateKey ) )
        {
            return false;
        }

        final PageTemplateKey that = (PageTemplateKey) o;

        return Objects.equals( this.siteTemplateKey, that.siteTemplateKey ) && Objects.equals( this.templateName, that.templateName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.siteTemplateKey, this.templateName );
    }
}
