package com.enonic.wem.core.content.page;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.page.DeletePage;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.Content.editContent;

public class DeletePageHandler
    extends CommandHandler<DeletePage>
{
    @Override
    public void handle()
        throws Exception
    {
        final UpdateContent updateContent = Commands.content().update().
            contentId( command.getContent() ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).page( null );
                }
            } );

        final Content updatedContent = context.getClient().execute( updateContent );
        command.setResult( updatedContent );
    }
}
