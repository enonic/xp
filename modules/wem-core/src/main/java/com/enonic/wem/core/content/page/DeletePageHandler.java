package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.page.DeletePage;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.Content.editContent;

public class DeletePageHandler
    extends CommandHandler<DeletePage>
{
    @Inject
    private ContentService contentService;

    @Override
    public void handle()
        throws Exception
    {
        final UpdateContentParams params = new UpdateContentParams().
            contentId( command.getContent() ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).page( null );
                }
            } );

        final Content updatedContent = contentService.update( params );
        command.setResult( updatedContent );
    }
}
