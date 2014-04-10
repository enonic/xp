package com.enonic.wem.api.content.site;


public interface SiteTemplateService
{
    SiteTemplates getSiteTemplates();

    SiteTemplate createSiteTemplate( CreateSiteTemplateParam param );

    SiteTemplate getSiteTemplate( SiteTemplateKey key )
        throws SiteTemplateNotFoundException;

    SiteTemplate updateSiteTemplate( UpdateSiteTemplateParam param )
        throws SiteTemplateNotFoundException;

    void deleteSiteTemplate( SiteTemplateKey key )
        throws SiteTemplateNotFoundException;
}
