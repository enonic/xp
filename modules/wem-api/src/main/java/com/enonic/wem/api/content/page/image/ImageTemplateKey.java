package com.enonic.wem.api.content.page.image;


import java.util.Objects;

import com.enonic.wem.api.content.site.SiteTemplateKey;

public class ImageTemplateKey
{
    private final SiteTemplateKey siteTemplateKey;

    private final ImageTemplateName templateName;

    public ImageTemplateKey( final SiteTemplateKey siteTemplateKey, final ImageTemplateName templateName )
    {
        this.siteTemplateKey = siteTemplateKey;
        this.templateName = templateName;
    }

    public SiteTemplateKey getSiteTemplateKey()
    {
        return siteTemplateKey;
    }

    public ImageTemplateName getTemplateName()
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
        if ( !( o instanceof ImageTemplateKey ) )
        {
            return false;
        }

        final ImageTemplateKey that = (ImageTemplateKey) o;

        return Objects.equals( this.siteTemplateKey, that.siteTemplateKey ) && Objects.equals( this.templateName, that.templateName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.siteTemplateKey, this.templateName );
    }
}
