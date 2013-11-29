package com.enonic.wem.core.content.page.layout;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.layout.GetLayoutTemplateByKey;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.core.command.CommandHandler;

public class GetLayoutTemplateByKeyHandler
    extends CommandHandler<GetLayoutTemplateByKey>
{
    @Override
    public void handle()
        throws Exception
    {
        final GetSiteTemplateByKey getSiteTemplateCommand = Commands.site().template().get().byKey( command.getKey().getSiteTemplateKey() );
        final SiteTemplate siteTemplate = context.getClient().execute( getSiteTemplateCommand );
        final LayoutTemplate template = siteTemplate.getLayoutTemplates().getTemplate( command.getKey().getTemplateName() );
        command.setResult( template );
    }
}
