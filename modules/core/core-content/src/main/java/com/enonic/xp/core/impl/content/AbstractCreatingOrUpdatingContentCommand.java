package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.processor.ContentProcessors;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.User;
import com.enonic.xp.site.SiteService;

class AbstractCreatingOrUpdatingContentCommand
    extends AbstractContentCommand
{
    private final static ImmutableList<MediaType> BINARY_CONTENT_TYPES =
        ImmutableList.of( MediaType.OCTET_STREAM, MediaType.create( "application", "force-download" ),
                          MediaType.create( "application", "x-force-download" ) );

    private final static ImmutableList<MediaType> EXECUTABLE_CONTENT_TYPES =
        ImmutableList.of( MediaType.OCTET_STREAM, MediaType.create( "text", "plain" ), MediaType.create( "application", "x-bzip2" ) );

    final MixinService mixinService;

    final SiteService siteService;

    final ContentProcessors contentProcessors;

    AbstractCreatingOrUpdatingContentCommand( final Builder builder )
    {
        super( builder );
        this.mixinService = builder.mixinService;
        this.siteService = builder.siteService;
        this.contentProcessors = builder.contentProcessors;
    }

    public static class Builder<B extends Builder>
        extends AbstractContentCommand.Builder<B>
    {
        private MixinService mixinService;

        private SiteService siteService;

        private ContentProcessors contentProcessors;

        Builder()
        {
        }

        Builder( final AbstractCreatingOrUpdatingContentCommand source )
        {
            super( source );
            this.mixinService = source.mixinService;
            this.siteService = source.siteService;
            this.contentProcessors = source.contentProcessors;
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

        @SuppressWarnings("unchecked")
        B contentProcessors( final ContentProcessors contentProcessors )
        {
            this.contentProcessors = contentProcessors;
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

    protected boolean isExecutableContentType( final String contentType, final String fileName )
    {
        final MediaType mediaType = MediaType.parse( contentType );
        return EXECUTABLE_CONTENT_TYPES.stream().anyMatch( mediaType::is ) && isExecutableFileName( fileName );
    }

    private boolean isExecutableFileName( final String fileName )
    {
        return fileName.endsWith( ".exe" ) || fileName.endsWith( ".msi" ) || fileName.endsWith( ".dmg" ) ||
            fileName.endsWith( ".bat" ) || fileName.endsWith( ".sh" );
    }
}


