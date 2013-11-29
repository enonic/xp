package com.enonic.wem.core.content.page.part;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.part.UpdatePartTemplate;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.command.content.site.UpdateSiteTemplate;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateEditor;
import com.enonic.wem.api.content.page.part.PartTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateEditor;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.core.command.CommandHandler;

public class UpdatePartTemplateHandler
    extends CommandHandler<UpdatePartTemplate>
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

        final PartTemplate template = siteTemplate.getPartTemplates().getTemplate( command.getKey().getTemplateName() );
        if ( template == null )
        {
            throw new PartTemplateNotFoundException( command.getKey() );
        }

        final PartTemplateEditor editor = command.getEditor();
        final PartTemplate.PartTemplateEditBuilder editBuilder = editor.edit( template );

        if ( editBuilder.isChanges() )
        {
            final PartTemplate editedTemplate = editBuilder.build();

            final UpdateSiteTemplate updateCommand = Commands.site().template().update().
                key( command.getKey().getSiteTemplateKey() ).
                editor( new SiteTemplateEditor()
                {
                    @Override
                    public SiteTemplate.SiteTemplateEditBuilder edit( final SiteTemplate toBeEdited )
                    {
                        return SiteTemplate.editSiteTemplate( toBeEdited ).setPartTemplate( editedTemplate );
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
