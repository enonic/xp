package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;

import static com.enonic.wem.api.content.Content.editContent;

final class DeletePageCommand
{
    private ContentService contentService;

    private ContentId contentId;

    public Content execute()
    {
        final UpdateContentParams params = new UpdateContentParams().
            contentId( this.contentId ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).page( null );
                }
            } );

        return this.contentService.update( params, ContentConstants.CONTEXT_STAGE );
    }

    public DeletePageCommand contentId( ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public DeletePageCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
