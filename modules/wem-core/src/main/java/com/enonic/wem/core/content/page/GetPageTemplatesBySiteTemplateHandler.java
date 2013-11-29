package com.enonic.wem.core.content.page;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.GetPageTemplatesBySiteTemplate;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.core.command.CommandHandler;

public class GetPageTemplatesBySiteTemplateHandler
    extends CommandHandler<GetPageTemplatesBySiteTemplate>
{
    @Override
    public void handle()
        throws Exception
    {
        final GetSiteTemplateByKey getSiteTemplateCommand = Commands.site().template().get().byKey( command.getSiteTemplate() );
        final SiteTemplate siteTemplate = context.getClient().execute( getSiteTemplateCommand );
        command.setResult( siteTemplate.getPageTemplates() );
    }
}
