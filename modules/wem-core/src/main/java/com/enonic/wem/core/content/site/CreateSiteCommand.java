package com.enonic.wem.core.content.site;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.context.Context;

import static com.enonic.wem.api.content.Content.editContent;

final class CreateSiteCommand
{
    private final CreateSiteParams params;

    private final ContentService contentService;

    private final Context context;

    private CreateSiteCommand( Builder builder )
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
        final Site site = Site.newSite().
            template( this.params.getTemplate() ).
            moduleConfigs( this.params.getModuleConfigs() ).
            build();

        final UpdateContentParams params = new UpdateContentParams().
            contentId( this.params.getContent() ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).site( site );
                }
            } );

        return this.contentService.update( params, this.context );
    }


    public static final class Builder
    {
        private CreateSiteParams params;

        private ContentService contentService;

        private Context context;

        private Builder()
        {
        }

        public Builder params( CreateSiteParams params )
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

        public CreateSiteCommand build()
        {
            validate();
            return new CreateSiteCommand( this );
        }
    }
}
