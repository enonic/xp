package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageNotFoundException;
import com.enonic.wem.api.content.page.UpdatePageParams;

import static com.enonic.wem.api.content.Content.editContent;

final class UpdatePageCommand
{
    private UpdatePageParams params;

    private ContentService contentService;

    public Content execute()
    {
        final Content content = this.contentService.getById( this.params.getContent() );

        if ( content == null )
        {
            throw new ContentNotFoundException( this.params.getContent() );
        }
        if ( content.getPage() == null )
        {
            throw new PageNotFoundException( this.params.getContent() );
        }

        Page.PageEditBuilder editBuilder = this.params.getEditor().edit( content.getPage() );

        if ( editBuilder.isChanges() )
        {
            final Page editedPage = editBuilder.build();

            final UpdateContentParams params = new UpdateContentParams().
                contentId( this.params.getContent() ).
                editor( new ContentEditor()
                {
                    @Override
                    public Content.EditBuilder edit( final Content toBeEdited )
                    {
                        return editContent( toBeEdited ).page( editedPage );
                    }
                } );

            this.contentService.update( params );
        }

        return this.contentService.getById( this.params.getContent() );
    }

    public UpdatePageCommand params( final UpdatePageParams params )
    {
        this.params = params;
        return this;
    }

    public UpdatePageCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
