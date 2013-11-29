package com.enonic.wem.core.content.page;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.UpdatePageTemplate;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.command.content.site.UpdateSiteTemplate;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateEditor;
import com.enonic.wem.api.content.page.PageTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateEditor;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.core.command.CommandHandler;

public class UpdatePageTemplateHandler
    extends CommandHandler<UpdatePageTemplate>
{
    @Override
    public void handle()
        throws Exception
    {
        final GetSiteTemplateByKey getSiteTemplateCommand = Commands.site().template().get().byKey( command.getKey().getSiteTemplateKey() );
        final SiteTemplate siteTemplate = context.getClient().execute( getSiteTemplateCommand );
        if ( siteTemplate == null )
        {
            throw new SiteTemplateNotFoundException( command.getKey().getSiteTemplateKey() );
        }

        final PageTemplate template = siteTemplate.getPageTemplates().getTemplate( command.getKey().getTemplateName() );
        if ( template == null )
        {
            throw new PageTemplateNotFoundException( command.getKey() );
        }

        final PageTemplateEditor editor = command.getEditor();
        final PageTemplate.PageTemplateEditBuilder editBuilder = editor.edit( template );

        if ( editBuilder.isChanges() )
        {
            final PageTemplate editedTemplate = editBuilder.build();

            final UpdateSiteTemplate updateCommand = Commands.site().template().update().
                key( command.getKey().getSiteTemplateKey() ).
                editor( new SiteTemplateEditor()
                {
                    @Override
                    public SiteTemplate.SiteTemplateEditBuilder edit( final SiteTemplate toBeEdited )
                    {
                        return SiteTemplate.editSiteTemplate( toBeEdited ).setPageTemplate( editedTemplate );
                    }
                } );

            context.getClient().execute( updateCommand );
            command.setResult( editedTemplate );
        }
        else
        {
            command.setResult( template );
        }
    }
}
