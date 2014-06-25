package com.enonic.wem.core.content.page;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.context.Context;

import static com.enonic.wem.api.content.Content.editContent;
import static com.enonic.wem.api.content.page.Page.newPage;

final class CreatePageCommand
{
    private final CreatePageParams params;

    private final ContentService contentService;

    private final Context context;

    private CreatePageCommand( Builder builder )
    {
        params = builder.params;
        contentService = builder.contentService;
        context = builder.context;
    }

    public static Builder create()
    {
        return new Builder();
    }

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

        return this.contentService.update( params, context );
    }


    public static final class Builder
    {
        private CreatePageParams params;

        private ContentService contentService;

        private Context context;

        private Builder()
        {
        }

        public Builder params( CreatePageParams params )
        {
            this.params = params;
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

        private void validate()
        {
            Preconditions.checkNotNull( context );
            Preconditions.checkNotNull( contentService );
            Preconditions.checkNotNull( params );
        }

        public CreatePageCommand build()
        {
            validate();
            return new CreatePageCommand( this );
        }
    }
}
