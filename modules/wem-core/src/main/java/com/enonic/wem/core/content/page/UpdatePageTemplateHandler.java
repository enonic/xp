package com.enonic.wem.core.content.page;


import javax.inject.Inject;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.UpdatePageTemplate;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateEditor;
import com.enonic.wem.api.content.page.PageTemplateNotFoundException;
import com.enonic.wem.api.content.site.SetSiteTemplateEditor;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.content.site.UpdateSiteTemplateParam;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.site.SetSiteTemplateEditor.newEditor;

public class UpdatePageTemplateHandler
    extends CommandHandler<UpdatePageTemplate>
{
    private SiteTemplateService siteTemplateService;

    @Override
    public void handle()
        throws Exception
    {
        final GetSiteTemplateByKey getSiteTemplateCommand = Commands.site().template().get().byKey( command.getSiteTemplateKey() );
        final SiteTemplate siteTemplate = context.getClient().execute( getSiteTemplateCommand );
        if ( siteTemplate == null )
        {
            throw new SiteTemplateNotFoundException( command.getSiteTemplateKey() );
        }

        final PageTemplate template = siteTemplate.getPageTemplates().getTemplate( command.getKey().getTemplateName() );
        if ( template == null )
        {
            throw new PageTemplateNotFoundException( command.getKey() );
        }

        final PageTemplateEditor editor = command.getEditor();
        final PageTemplate updatedTemplate = editor.edit( template );

        if ( updatedTemplate == null || updatedTemplate == template )
        {
            command.setResult( template );
        }
        else
        {
            final SetSiteTemplateEditor siteTemplateEditor = newEditor().addTemplate( updatedTemplate ).build();
            final UpdateSiteTemplateParam updateSiteParams = new UpdateSiteTemplateParam().
                key( command.getSiteTemplateKey() ).
                editor( siteTemplateEditor );
            this.siteTemplateService.updateSiteTemplate( updateSiteParams );

            command.setResult( updatedTemplate );
        }
    }

    @Inject
    public void setSiteTemplateService( final SiteTemplateService siteTemplateService )
    {
        this.siteTemplateService = siteTemplateService;
    }
}
