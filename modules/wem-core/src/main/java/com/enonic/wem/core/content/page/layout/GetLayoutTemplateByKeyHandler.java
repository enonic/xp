package com.enonic.wem.core.content.page.layout;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.layout.GetLayoutTemplateByKey;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.core.command.CommandHandler;

public class GetLayoutTemplateByKeyHandler
    extends CommandHandler<GetLayoutTemplateByKey>
{
    @Override
    public void handle()
        throws Exception
    {
        final LayoutTemplateKey layoutTemplateKey = command.getKey();
        final SiteTemplateKey siteTemplateKey = command.getSiteTemplateKey();
        final GetSiteTemplateByKey getSiteTemplateCommand = Commands.site().template().get().byKey( siteTemplateKey );
        final SiteTemplate siteTemplate = context.getClient().execute( getSiteTemplateCommand );
        final LayoutTemplate layoutTemplate = siteTemplate.getLayoutTemplates().getTemplate( layoutTemplateKey.getTemplateName() );

        if ( layoutTemplate == null )
        {
            throw new LayoutTemplateNotFoundException( layoutTemplateKey );
        }
        command.setResult( layoutTemplate );
    }
}
