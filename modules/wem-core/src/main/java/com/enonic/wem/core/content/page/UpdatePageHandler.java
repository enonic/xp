package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageNotFoundException;
import com.enonic.wem.api.content.page.UpdatePage;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.Content.editContent;

public class UpdatePageHandler
    extends CommandHandler<UpdatePage>
{
    @Inject
    private ContentService contentService;

    @Override
    public void handle()
        throws Exception
    {
        final Content content = contentService.getById( command.getContent() );

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

            final UpdateContentParams params = new UpdateContentParams().
                contentId( command.getContent() ).
                editor( new ContentEditor()
                {
                    @Override
                    public Content.EditBuilder edit( final Content toBeEdited )
                    {
                        return editContent( toBeEdited ).page( editedPage );
                    }
                } );

            contentService.update( params );
        }

        final Content updatedContent = contentService.getById( command.getContent() );

        command.setResult( updatedContent );
    }
}
