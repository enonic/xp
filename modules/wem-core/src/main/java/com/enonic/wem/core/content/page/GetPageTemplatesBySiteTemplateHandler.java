package com.enonic.wem.core.content.page;


import javax.inject.Inject;

import com.enonic.wem.api.content.page.GetPageTemplatesBySiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.core.command.CommandHandler;

public class GetPageTemplatesBySiteTemplateHandler
    extends CommandHandler<GetPageTemplatesBySiteTemplate>
{
    @Inject
    protected SiteTemplateService siteTemplateService;

    @Override
    public void handle()
        throws Exception
    {
        final SiteTemplate siteTemplate = this.siteTemplateService.getSiteTemplate( command.getSiteTemplate() );
        command.setResult( siteTemplate.getPageTemplates() );
    }
}
