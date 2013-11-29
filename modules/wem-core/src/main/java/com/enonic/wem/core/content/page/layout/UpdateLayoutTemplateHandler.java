package com.enonic.wem.core.content.page.layout;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.layout.UpdateLayoutTemplate;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.command.content.site.UpdateSiteTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateEditor;
import com.enonic.wem.api.content.page.layout.LayoutTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateEditor;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.core.command.CommandHandler;

public class UpdateLayoutTemplateHandler
    extends CommandHandler<UpdateLayoutTemplate>
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

        final LayoutTemplate template = siteTemplate.getLayoutTemplates().getTemplate( command.getKey().getTemplateName() );
        if ( template == null )
        {
            throw new LayoutTemplateNotFoundException( command.getKey() );
        }

        final LayoutTemplateEditor editor = command.getEditor();
        final LayoutTemplate.LayoutTemplateEditBuilder editBuilder = editor.edit( template );

        if ( editBuilder.isChanges() )
        {
            final LayoutTemplate editedTemplate = editBuilder.build();

            final UpdateSiteTemplate updateCommand = Commands.site().template().update().
                key( command.getKey().getSiteTemplateKey() ).
                editor( new SiteTemplateEditor()
                {
                    @Override
                    public SiteTemplate.SiteTemplateEditBuilder edit( final SiteTemplate toBeEdited )
                    {
                        return SiteTemplate.editSiteTemplate( toBeEdited ).setLayoutTemplate( editedTemplate );
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
