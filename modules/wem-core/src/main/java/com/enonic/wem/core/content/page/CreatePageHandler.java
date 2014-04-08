package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.page.CreatePage;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.Content.editContent;
import static com.enonic.wem.api.content.page.Page.newPage;

public class CreatePageHandler
    extends CommandHandler<CreatePage>
{
    @Inject
    private ContentService contentService;

    @Override
    public void handle()
        throws Exception
    {
        final Page page = newPage().
            template( command.getPageTemplate() ).
            config( command.getConfig() ).
            regions( command.getRegions() ).
            build();

        final UpdateContentParams params = new UpdateContentParams().
            contentId( command.getContent() ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).page( page );
                }
            } );

        final Content updatedContent = contentService.update( params );
        command.setResult( updatedContent );
    }
}
