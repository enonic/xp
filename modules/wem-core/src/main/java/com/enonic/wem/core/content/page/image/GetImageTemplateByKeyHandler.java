package com.enonic.wem.core.content.page.image;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.image.GetImageTemplateByKey;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.core.command.CommandHandler;

public class GetImageTemplateByKeyHandler
    extends CommandHandler<GetImageTemplateByKey>
{
    @Override
    public void handle()
        throws Exception
    {
        final GetSiteTemplateByKey getSiteTemplateCommand = Commands.site().template().get().byKey( command.getKey().getSiteTemplateKey() );
        final SiteTemplate siteTemplate = context.getClient().execute( getSiteTemplateCommand );
        final ImageTemplate imageTemplate = siteTemplate.getImageTemplates().getTemplate( command.getKey().getTemplateName() );
        command.setResult( imageTemplate );
    }
}
