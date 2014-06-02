package com.enonic.wem.core.content.site;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;

import static com.enonic.wem.api.content.Content.editContent;

final class DeleteSiteCommand
{
    private ContentId contentId;

    private ContentService contentService;

    public Content execute()
    {
        final UpdateContentParams params = new UpdateContentParams().
            contentId( this.contentId ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).site( null );
                }
            } );

        this.contentService.update( params, ContentConstants.DEFAULT_CONTEXT);

        return this.contentService.getById( this.contentId, ContentConstants.DEFAULT_CONTEXT);
    }

    public DeleteSiteCommand contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public DeleteSiteCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
