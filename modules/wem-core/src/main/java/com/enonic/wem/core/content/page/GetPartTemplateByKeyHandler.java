package com.enonic.wem.core.content.page;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.part.GetPartTemplateByKey;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.core.command.CommandHandler;

public class GetPartTemplateByKeyHandler
    extends CommandHandler<GetPartTemplateByKey>
{
    @Override
    public void handle()
        throws Exception
    {
        final GetSiteTemplateByKey getSiteTemplateCommand = Commands.site().template().get().byKey( command.getKey().getSiteTemplateKey() );
        final SiteTemplate siteTemplate = context.getClient().execute( getSiteTemplateCommand );
        final PartTemplate imageTemplate = siteTemplate.getPartTemplates().getTemplate( command.getKey().getTemplateName() );
        command.setResult( imageTemplate );
    }
}
