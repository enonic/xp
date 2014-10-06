package com.enonic.wem.core.content.page;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.context.Context;

import static com.enonic.wem.api.content.Content.editContent;

final class DeletePageCommand
{
    private final ContentService contentService;

    private final ContentId contentId;

    private final Context context;

    private DeletePageCommand( Builder builder )
    {
        contentService = builder.contentService;
        contentId = builder.contentId;
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
                    return editContent( toBeEdited ).page( null );
                }
            } );

        return this.contentService.update( params );
    }


    public static final class Builder
    {
        private ContentService contentService;

        private ContentId contentId;

        private Context context;

        private Builder()
        {
        }

        public Builder contentService( ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder context( Context context )
        {
            this.context = context;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.contentId );
            Preconditions.checkNotNull( this.context );
            Preconditions.checkNotNull( this.contentService );
        }

        public DeletePageCommand build()
        {
            validate();
            return new DeletePageCommand( this );
        }
    }
}
