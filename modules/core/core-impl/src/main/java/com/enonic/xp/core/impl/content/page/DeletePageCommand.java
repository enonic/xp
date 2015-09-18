package com.enonic.xp.core.impl.content.page;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.UpdateContentParams;

final class DeletePageCommand
{
    private final ContentService contentService;

    private final ContentId contentId;

    private DeletePageCommand( Builder builder )
    {
        contentService = builder.contentService;
        contentId = builder.contentId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content execute()
    {
        final UpdateContentParams params = new UpdateContentParams();
        params.contentId( this.contentId );
        params.editor( edit -> edit.page = null );

        return this.contentService.update( params );
    }


    public static final class Builder
    {
        private ContentService contentService;

        private ContentId contentId;

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

        private void validate()
        {
            Preconditions.checkNotNull( this.contentId );
            Preconditions.checkNotNull( this.contentService );
        }

        public DeletePageCommand build()
        {
            validate();
            return new DeletePageCommand( this );
        }
    }
}
