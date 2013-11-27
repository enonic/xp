package com.enonic.wem.core.content.site;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.site.UpdateSite;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteNotFoundException;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.Content.editContent;

public class UpdateSiteHandler
    extends CommandHandler<UpdateSite>
{
    @Override
    public void handle()
        throws Exception
    {
        final Content content =
            context.getClient().execute( Commands.content().get().selectors( ContentIds.from( command.getContent() ) ) ).first();

        if ( content == null )
        {
            throw new ContentNotFoundException( command.getContent() );
        }
        if ( content.getSite() == null )
        {
            throw new SiteNotFoundException( command.getContent() );
        }

        Site.EditBuilder editBuilder = command.getEditor().edit( content.getSite() );

        if ( editBuilder.isChanges() )
        {
            final Site editedSite = editBuilder.build();

            final UpdateContent updateContent = Commands.content()
                .update()
                .modifier( AccountKey.anonymous() )
                .selector( command.getContent() )
                .editor( new ContentEditor()
                {
                    @Override
                    public Content.EditBuilder edit( final Content toBeEdited )
                    {
                        return editContent( toBeEdited ).site( editedSite );
                    }
                } );

            final Content.EditBuilder builder = updateContent.getEditor().edit( content );

            final Content result = builder.build();
            command.setResult( result );
        }
    }
}
