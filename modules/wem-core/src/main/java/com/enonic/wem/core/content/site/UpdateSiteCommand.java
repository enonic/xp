package com.enonic.wem.core.content.site;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteNotFoundException;
import com.enonic.wem.api.content.site.UpdateSiteParams;
import com.enonic.wem.api.context.Context;

import static com.enonic.wem.api.content.Content.editContent;

final class UpdateSiteCommand
{
    private final UpdateSiteParams params;

    private final ContentService contentService;

    private final Context context;

    private UpdateSiteCommand( Builder builder )
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
        final Content content = this.contentService.getById( this.params.getContent(), this.context );

        if ( content == null )
        {
            throw new ContentNotFoundException( this.params.getContent(), this.context.getWorkspace() );
        }
        if ( content.getSite() == null )
        {
            throw new SiteNotFoundException( this.params.getContent() );
        }

        Site.SiteEditBuilder editBuilder = this.params.getEditor().edit( content.getSite() );

        if ( editBuilder.isChanges() )
        {
            final Site editedSite = editBuilder.build();

            final UpdateContentParams updateContent = new UpdateContentParams().
                contentId( this.params.getContent() ).
                editor( new ContentEditor()
                {
                    @Override
                    public Content.EditBuilder edit( final Content toBeEdited )
                    {
                        return editContent( toBeEdited ).site( editedSite );
                    }
                } );

            this.contentService.update( updateContent, this.context );
        }

        return this.contentService.getById( this.params.getContent(), this.context );
    }


    public static final class Builder
    {
        private UpdateSiteParams params;

        private ContentService contentService;

        private Context context;

        private Builder()
        {
        }

        public Builder params( UpdateSiteParams params )
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
            Preconditions.checkNotNull( this.context );
            Preconditions.checkNotNull( this.params );
            Preconditions.checkNotNull( this.contentService );

        }

        public UpdateSiteCommand build()
        {
            validate();
            return new UpdateSiteCommand( this );
        }
    }
}
