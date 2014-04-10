package com.enonic.wem.api.content.site;


public interface SiteTemplateService
{
    SiteTemplates getSiteTemplates();

    SiteTemplate createSiteTemplate( CreateSiteTemplateParams param );

    SiteTemplate getSiteTemplate( SiteTemplateKey key )
        throws SiteTemplateNotFoundException;

    SiteTemplate updateSiteTemplate( UpdateSiteTemplateParams param )
        throws SiteTemplateNotFoundException;

    void deleteSiteTemplate( SiteTemplateKey key )
        throws SiteTemplateNotFoundException;
}
