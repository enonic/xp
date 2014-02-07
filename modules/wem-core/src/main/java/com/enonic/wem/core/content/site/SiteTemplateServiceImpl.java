package com.enonic.wem.core.content.site;


import javax.inject.Inject;

import com.enonic.wem.api.content.site.CreateSiteTemplateParam;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.content.site.UpdateSiteTemplateParam;
import com.enonic.wem.core.config.SystemConfig;

public final class SiteTemplateServiceImpl
    implements SiteTemplateService
{
    @Inject
    protected SystemConfig systemConfig;

    @Inject
    protected SiteTemplateExporter siteTemplateExporter;

    @Override
    public SiteTemplate createSiteTemplate( final CreateSiteTemplateParam param )
    {
        return new CreateSiteTemplateCommand().
            param( param ).
            systemConfig( this.systemConfig ).
            siteTemplateExporter( this.siteTemplateExporter ).
            execute();
    }

    @Override
    public boolean updateSiteTemplate( final UpdateSiteTemplateParam param  )
        throws SiteTemplateNotFoundException
    {
        return new UpdateSiteTemplateCommand().
            param( param ).
            systemConfig( this.systemConfig ).
            siteTemplateExporter( this.siteTemplateExporter ).
            execute();
    }
}
