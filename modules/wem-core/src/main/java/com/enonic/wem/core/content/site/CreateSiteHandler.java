package com.enonic.wem.core.content.site;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentNotFoundException;
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
        final Content content =
            context.getClient().execute( Commands.content().get().selectors( ContentIds.from( command.getContent() ) ) ).first();

        if ( content == null )
        {
            throw new ContentNotFoundException( command.getContent() );
        }

        final CreateSite createSite = new CreateSite().
            content( command.getContent() ).
            template( command.getTemplate() );

        context.getClient().execute( createSite );
        final Site createdSite = createSite.getResult().getSite();

        final UpdateContent updateContent = Commands.content()
            .update()
            .modifier( AccountKey.anonymous() )
            .selector( command.getContent() )
            .editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).site( createdSite );
                }
            } );

        context.getClient().execute( updateContent );
    }
}
