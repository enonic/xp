package com.enonic.wem.core.content.page;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.page.EditablePage;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageNotFoundException;
import com.enonic.wem.api.content.page.UpdatePageParams;
import com.enonic.wem.api.context.ContextAccessor;

final class UpdatePageCommand
{
    private final UpdatePageParams params;

    private final ContentService contentService;

    private UpdatePageCommand( Builder builder )
    {
        params = builder.params;
        contentService = builder.contentService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content execute()
    {
        final Content content = this.contentService.getById( this.params.getContent() );

        if ( content == null )
        {
            throw new ContentNotFoundException( this.params.getContent(), ContextAccessor.current().getBranch() );
        }
        if ( content.getPage() == null )
        {
            throw new PageNotFoundException( this.params.getContent() );
        }

        final EditablePage editablePage = new EditablePage( content.getPage() );
        this.params.getEditor().edit( editablePage );
        final Page editedPage = editablePage.build();

        if ( !editedPage.equals( content.getPage() ) )
        {
            final UpdateContentParams params = new UpdateContentParams().
                contentId( this.params.getContent() ).
                editor( edit -> edit.page = editedPage );

            this.contentService.update( params );
        }

        return this.contentService.getById( this.params.getContent() );
    }


    public static final class Builder
    {
        private UpdatePageParams params;

        private ContentService contentService;

        private Builder()
        {
        }

        public Builder params( UpdatePageParams params )
        {
            this.params = params;
            return this;
        }

        public Builder contentService( ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentService );
            Preconditions.checkNotNull( params );
        }

        public UpdatePageCommand build()
        {
            validate();
            return new UpdatePageCommand( this );
        }
    }
}
