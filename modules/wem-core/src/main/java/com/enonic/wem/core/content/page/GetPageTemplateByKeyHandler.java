package com.enonic.wem.core.content.page;


import javax.inject.Inject;

import com.enonic.wem.api.content.page.GetPageTemplateByKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.core.command.CommandHandler;

public class GetPageTemplateByKeyHandler
    extends CommandHandler<GetPageTemplateByKey>
{
    @Inject
    protected SiteTemplateService siteTemplateService;

    @Override
    public void handle()
        throws Exception
    {
        final PageTemplateKey pageTemplateKey = command.getKey();
        final SiteTemplateKey siteTemplateKey = command.getSiteTemplateKey();
        final SiteTemplate siteTemplate = this.siteTemplateService.getSiteTemplate( siteTemplateKey );
        final PageTemplate pageTemplate = siteTemplate.getPageTemplates().getTemplate( pageTemplateKey.getTemplateName() );

        if ( pageTemplate == null )
        {
            throw new PageTemplateNotFoundException( pageTemplateKey );
        }
        command.setResult( pageTemplate );
    }
}
