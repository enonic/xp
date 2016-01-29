package com.enonic.xp.core.impl.content.page.region;

import java.util.UUID;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.Page;
import com.enonic.xp.region.CreateFragmentParams;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.User;

final class CreateFragmentCommand
{
    private final CreateFragmentParams params;

    private final ContentService contentService;

    private CreateFragmentCommand( Builder builder )
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
        final String displayName = "Fragment - " + params.getComponent().getName();
        final String name = "fragment-" + UUID.randomUUID().toString();
        final CreateContentParams createContent = CreateContentParams.create().
            parent( params.getParent() ).
            displayName( displayName ).
            name( name ).
            type( ContentTypeName.fragment() ).
            contentData( new PropertyTree() ).
            build();
        final Content content = contentService.create( createContent );

        final Page page = Page.create().
            config( this.params.getConfig() ).
            fragment( this.params.getComponent() ).
            build();

        final UpdateContentParams params = new UpdateContentParams().
            contentId( content.getId() ).
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
        private CreateFragmentParams params;

        private ContentService contentService;

        private Builder()
        {
        }

        public Builder params( CreateFragmentParams params )
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

        public CreateFragmentCommand build()
        {
            validate();
            return new CreateFragmentCommand( this );
        }
    }
}
