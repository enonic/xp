package com.enonic.wem.core.content.page.part;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.part.GetPartTemplateByKey;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.content.page.part.PartTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.core.command.CommandHandler;

public class GetPartTemplateByKeyHandler
    extends CommandHandler<GetPartTemplateByKey>
{
    @Override
    public void handle()
        throws Exception
    {
        final PartTemplateKey partTemplateKey = command.getKey();
        final SiteTemplateKey siteTemplateKey = partTemplateKey.getSiteTemplateKey();
        final GetSiteTemplateByKey getSiteTemplateCommand = Commands.site().template().get().byKey( siteTemplateKey );
        final SiteTemplate siteTemplate = context.getClient().execute( getSiteTemplateCommand );

        if ( siteTemplate == null )
        {
            throw new SiteTemplateNotFoundException( siteTemplateKey );
        }

        final PartTemplate partTemplate = siteTemplate.getPartTemplates().getTemplate( partTemplateKey.getTemplateName() );

        if ( partTemplate == null )
        {
            throw new PartTemplateNotFoundException( partTemplateKey );
        }
        command.setResult( partTemplate );
    }
}
