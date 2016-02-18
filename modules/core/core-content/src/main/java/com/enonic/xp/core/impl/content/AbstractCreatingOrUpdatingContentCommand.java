package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.User;
import com.enonic.xp.site.SiteService;

class AbstractCreatingOrUpdatingContentCommand
    extends AbstractContentCommand
{
    private final static ImmutableList<MediaType> BINARY_CONTENT_TYPES =
        ImmutableList.of( MediaType.OCTET_STREAM, MediaType.create( "application", "force-download" ),
                          MediaType.create( "application", "x-force-download" ) );

    final MixinService mixinService;

    final SiteService siteService;

    AbstractCreatingOrUpdatingContentCommand( final Builder builder )
    {
        super( builder );
        this.mixinService = builder.mixinService;
        this.siteService = builder.siteService;
    }

    public static class Builder<B extends Builder>
        extends AbstractContentCommand.Builder<B>
    {
        private MixinService mixinService;

        private SiteService siteService;

        Builder()
        {
        }

        Builder( final AbstractCreatingOrUpdatingContentCommand source )
        {
            super( source );
            this.mixinService = source.mixinService;
            this.siteService = source.siteService;
        }

        @SuppressWarnings("unchecked")
        B mixinService( final MixinService mixinService )
        {
            this.mixinService = mixinService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B siteService( final SiteService siteService )
        {
            this.siteService = siteService;
            return (B) this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( mixinService, "mixinService cannot be null" );
        }
    }

    User getCurrentUser()
    {
        final Context context = ContextAccessor.current();

        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser() : User.ANONYMOUS;
    }

    protected boolean isBinaryContentType( final String contentType )
    {
        final MediaType mediaType = MediaType.parse( contentType );
        return BINARY_CONTENT_TYPES.stream().anyMatch( mediaType::is );
    }
}


