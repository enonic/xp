package com.enonic.wem.core.content.page;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.security.User;

import static com.enonic.wem.api.content.page.Page.newPage;

final class CreatePageCommand
{
    private final CreatePageParams params;

    private final ContentService contentService;

    private CreatePageCommand( Builder builder )
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
        this.params.validate();

        final Page page = newPage().
            controller( this.params.getController() ).
            template( this.params.getPageTemplate() ).
            config( this.params.getConfig() ).
            regions( this.params.getRegions() ).
            build();

        final UpdateContentParams params = new UpdateContentParams().
            contentId( this.params.getContent() ).
            modifier( getCurrentUser().getKey() ).
            editor( edit -> edit.page = page );

        return this.contentService.update( params );
    }

    User getCurrentUser()
    {
        final Context context = ContextAccessor.current();

        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser() : User.ANONYMOUS;
    }

    public static final class Builder
    {
        private CreatePageParams params;

        private ContentService contentService;

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

        private void validate()
        {
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
