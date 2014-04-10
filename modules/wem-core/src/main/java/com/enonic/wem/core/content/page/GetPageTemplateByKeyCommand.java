package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;

final class GetPageTemplateByKeyCommand
{
    private PageTemplateKey pageTemplateKey;

    private SiteTemplateKey siteTemplateKey;

    private SiteTemplateService siteTemplateService;

    public PageTemplate execute()
    {
        final SiteTemplate siteTemplate = this.siteTemplateService.getSiteTemplate( this.siteTemplateKey );
        final PageTemplate pageTemplate = siteTemplate.getPageTemplates().getTemplate( this.pageTemplateKey.getTemplateName() );

        if ( pageTemplate == null )
        {
            throw new PageTemplateNotFoundException( this.pageTemplateKey );
        }

        return pageTemplate;
    }

    public GetPageTemplateByKeyCommand pageTemplateKey( final PageTemplateKey pageTemplateKey )
    {
        this.pageTemplateKey = pageTemplateKey;
        return this;
    }

    public GetPageTemplateByKeyCommand siteTemplateKey( final SiteTemplateKey siteTemplateKey )
    {
        this.siteTemplateKey = siteTemplateKey;
        return this;
    }

    public GetPageTemplateByKeyCommand siteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
        return this;
    }
}
