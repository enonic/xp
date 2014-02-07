package com.enonic.wem.core.content.site;


import javax.inject.Inject;

import com.enonic.wem.api.content.site.CreateSiteTemplateSpec;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.core.config.SystemConfig;

public final class SiteTemplateServiceImpl
    implements SiteTemplateService
{
    @Inject
    protected SystemConfig systemConfig;

    @Inject
    protected SiteTemplateExporter siteTemplateExporter;

    @Override
    public SiteTemplate createSiteTemplate( final CreateSiteTemplateSpec spec )
    {
        return new CreateSiteTemplateCommand().
            spec( spec ).
            systemConfig( this.systemConfig ).
            siteTemplateExporter( this.siteTemplateExporter ).
            execute();
    }

}
