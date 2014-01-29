package com.enonic.wem.core.content.page;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.GetPageTemplateByKey;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.core.command.CommandHandler;

public class GetPageTemplateByKeyHandler
    extends CommandHandler<GetPageTemplateByKey>
{
    @Override
    public void handle()
        throws Exception
    {
        final PageTemplateKey pageTemplateKey = command.getKey();
        final SiteTemplateKey siteTemplateKey = command.getSiteTemplateKey();
        final GetSiteTemplateByKey getSiteTemplateCommand = Commands.site().template().get().byKey( siteTemplateKey );
        final SiteTemplate siteTemplate = context.getClient().execute( getSiteTemplateCommand );
        final PageTemplate pageTemplate = siteTemplate.getPageTemplates().getTemplate( pageTemplateKey.getTemplateName() );

        if ( pageTemplate == null )
        {
            throw new PageTemplateNotFoundException( pageTemplateKey );
        }
        command.setResult( pageTemplate );
    }
}
