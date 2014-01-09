package com.enonic.wem.core.content.page;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.page.CreatePage;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.Content.editContent;
import static com.enonic.wem.api.content.page.Page.newPage;

public class CreatePageHandler
    extends CommandHandler<CreatePage>
{
    @Override
    public void handle()
        throws Exception
    {
        final Page page = newPage().
            template( command.getPageTemplate() ).
            config( command.getConfig() ).
            regions( command.getRegions() ).
            build();

        final UpdateContent updateContent = Commands.content().update().
            contentId( command.getContent() ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).page( page );
                }
            } );

        final Content updatedContent = context.getClient().execute( updateContent );
        command.setResult( updatedContent );
    }
}
