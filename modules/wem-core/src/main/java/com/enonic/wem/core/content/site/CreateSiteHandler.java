package com.enonic.wem.core.content.site;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.Content.editContent;

public class CreateSiteHandler
    extends CommandHandler<CreateSite>
{
    @Override
    public void handle()
        throws Exception
    {
        final Site site = Site.newSite().
            template( command.getTemplate() ).
            moduleConfigs( command.getModuleConfigs() ).
            build();

        final UpdateContent updateContent = Commands.content().update().
            selector( command.getContent() ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).site( site );
                }
            } );

        context.getClient().execute( updateContent );

        context.getJcrSession().save();

        final Content updatedContent = context.getClient().execute( Commands.content().get().byId( command.getContent() ) );

        command.setResult( updatedContent );
    }
}
