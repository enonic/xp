package com.enonic.wem.core.content.page.image;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.image.UpdateImageTemplate;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.command.content.site.UpdateSiteTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateEditor;
import com.enonic.wem.api.content.page.image.ImageTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateEditor;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.core.command.CommandHandler;

public class UpdateImageTemplateHandler
    extends CommandHandler<UpdateImageTemplate>
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

        final ImageTemplate template = siteTemplate.getImageTemplates().getTemplate( command.getKey().getTemplateName() );
        if ( template == null )
        {
            throw new ImageTemplateNotFoundException( command.getKey() );
        }

        final ImageTemplateEditor editor = command.getEditor();
        final ImageTemplate.ImageTemplateEditBuilder editBuilder = editor.edit( template );

        if ( editBuilder.isChanges() )
        {
            final ImageTemplate editedTemplate = editBuilder.build();

            final UpdateSiteTemplate updateCommand = Commands.site().template().update().
                key( command.getKey().getSiteTemplateKey() ).
                editor( new SiteTemplateEditor()
                {
                    @Override
                    public SiteTemplate.SiteTemplateEditBuilder edit( final SiteTemplate toBeEdited )
                    {
                        return SiteTemplate.editSiteTemplate( toBeEdited ).setImageTemplate( editedTemplate );
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
