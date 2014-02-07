package com.enonic.wem.api.content.site;


public interface SiteTemplateService
{

    SiteTemplate createSiteTemplate( CreateSiteTemplateParam param );

    boolean updateSiteTemplate( UpdateSiteTemplateParam param )
        throws SiteTemplateNotFoundException;

}
