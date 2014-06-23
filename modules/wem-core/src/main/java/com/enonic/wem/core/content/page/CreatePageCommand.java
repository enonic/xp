package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.Page;

import static com.enonic.wem.api.content.Content.editContent;
import static com.enonic.wem.api.content.page.Page.newPage;

final class CreatePageCommand
{
    private CreatePageParams params;

    private ContentService contentService;

    public Content execute()
    {
        final Page page = newPage().
            template( this.params.getPageTemplate() ).
            config( this.params.getConfig() ).
            regions( this.params.getRegions() ).
            build();

        final UpdateContentParams params = new UpdateContentParams().
            contentId( this.params.getContent() ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).page( page );
                }
            } );

        return this.contentService.update( params, ContentConstants.CONTEXT_STAGE );
    }

    public CreatePageCommand params( final CreatePageParams params )
    {
        this.params = params;
        return this;
    }

    public CreatePageCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
