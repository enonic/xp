package com.enonic.wem.core.content.page.image;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.image.GetImageTemplatesBySiteTemplate;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.core.command.CommandHandler;

public class GetImageTemplatesBySiteTemplateHandler
    extends CommandHandler<GetImageTemplatesBySiteTemplate>
{
    @Override
    public void handle()
        throws Exception
    {
        final GetSiteTemplateByKey getSiteTemplateCommand = Commands.site().template().get().byKey( command.getSiteTemplate() );
        final SiteTemplate siteTemplate = context.getClient().execute( getSiteTemplateCommand );
        command.setResult( siteTemplate.getImageTemplates() );
    }
}
