package com.enonic.wem.core.content.site;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.context.Context;

import static com.enonic.wem.api.content.Content.editContent;

final class DeleteSiteCommand
{
    private final ContentId contentId;

    private final ContentService contentService;

    private final Context context;

    private DeleteSiteCommand( Builder builder )
    {
        contentId = builder.contentId;
        contentService = builder.contentService;
        context = builder.context;
    }

    public static Builder create()
    {
        return new Builder();
    }

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

        this.contentService.update( params, this.context );

        return this.contentService.getById( this.contentId, this.context );
    }


    public static final class Builder
    {
        private ContentId contentId;

        private ContentService contentService;

        private Context context;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder contentService( ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder context( Context context )
        {
            this.context = context;
            return this;
        }

        public DeleteSiteCommand build()
        {
            return new DeleteSiteCommand( this );
        }
    }
}
