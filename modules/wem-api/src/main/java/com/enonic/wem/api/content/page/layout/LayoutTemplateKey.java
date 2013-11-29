package com.enonic.wem.api.content.page.layout;


import java.util.Objects;

import com.enonic.wem.api.content.site.SiteTemplateKey;

public class LayoutTemplateKey
{
    private final SiteTemplateKey siteTemplateKey;

    private final LayoutTemplateName templateName;

    public LayoutTemplateKey( final SiteTemplateKey siteTemplateKey, final LayoutTemplateName templateName )
    {
        this.siteTemplateKey = siteTemplateKey;
        this.templateName = templateName;
    }

    public SiteTemplateKey getSiteTemplateKey()
    {
        return siteTemplateKey;
    }

    public LayoutTemplateName getTemplateName()
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
        if ( !( o instanceof LayoutTemplateKey ) )
        {
            return false;
        }

        final LayoutTemplateKey that = (LayoutTemplateKey) o;

        return Objects.equals( this.siteTemplateKey, that.siteTemplateKey ) && Objects.equals( this.templateName, that.templateName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.siteTemplateKey, this.templateName );
    }
}
