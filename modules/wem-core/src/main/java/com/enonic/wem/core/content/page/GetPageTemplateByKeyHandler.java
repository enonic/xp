package com.enonic.wem.core.content.page;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.GetPageTemplateByKey;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.core.command.CommandHandler;

public class GetPageTemplateByKeyHandler
    extends CommandHandler<GetPageTemplateByKey>
{
    @Override
    public void handle()
        throws Exception
    {
        final GetSiteTemplateByKey getSiteTemplateCommand = Commands.site().template().get().byKey( command.getKey().getSiteTemplateKey() );
        final SiteTemplate siteTemplate = context.getClient().execute( getSiteTemplateCommand );
        final PageTemplate imageTemplate = siteTemplate.getPageTemplates().getTemplate( command.getKey().getTemplateName() );
        command.setResult( imageTemplate );
    }
}
