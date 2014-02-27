package com.enonic.wem.api.content.site;


public interface SiteTemplateService
{

    SiteTemplate createSiteTemplate( CreateSiteTemplateParam param );

    SiteTemplate getSiteTemplate( SiteTemplateKey key )
        throws SiteTemplateNotFoundException;

    boolean updateSiteTemplate( UpdateSiteTemplateParam param )
        throws SiteTemplateNotFoundException;

}
