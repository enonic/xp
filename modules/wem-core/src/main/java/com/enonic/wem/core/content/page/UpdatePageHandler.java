package com.enonic.wem.core.content.page;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.page.UpdatePage;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageNotFoundException;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.Content.editContent;

public class UpdatePageHandler
    extends CommandHandler<UpdatePage>
{
    @Override
    public void handle()
        throws Exception
    {
        final Content content = context.getClient().execute( Commands.content().get().byId( command.getContent() ) );

        if ( content == null )
        {
            throw new ContentNotFoundException( command.getContent() );
        }
        if ( content.getPage() == null )
        {
            throw new PageNotFoundException( command.getContent() );
        }

        Page.PageEditBuilder editBuilder = command.getEditor().edit( content.getPage() );

        if ( editBuilder.isChanges() )
        {
            final Page editedPage = editBuilder.build();

            final UpdateContent updateContent = Commands.content().update().
                contentId( command.getContent() ).
                editor( new ContentEditor()
                {
                    @Override
                    public Content.EditBuilder edit( final Content toBeEdited )
                    {
                        return editContent( toBeEdited ).page( editedPage );
                    }
                } );

            context.getClient().execute( updateContent );
        }

        final Content updatedContent = context.getClient().execute( Commands.content().get().byId( command.getContent() ) );

        command.setResult( updatedContent );
    }
}
