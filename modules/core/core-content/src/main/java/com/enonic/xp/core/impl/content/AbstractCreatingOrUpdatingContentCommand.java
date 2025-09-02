package com.enonic.xp.core.impl.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.DigestInputStream;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.processor.ContentProcessor;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.core.internal.HexCoder;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.User;
import com.enonic.xp.site.SiteService;

class AbstractCreatingOrUpdatingContentCommand
    extends AbstractContentCommand
{
    private static final ImmutableList<MediaType> BINARY_CONTENT_TYPES =
        ImmutableList.of( MediaType.OCTET_STREAM, MediaType.create( "application", "force-download" ),
                          MediaType.create( "application", "x-force-download" ) );

    private static final ImmutableList<MediaType> EXECUTABLE_CONTENT_TYPES =
        ImmutableList.of( MediaType.OCTET_STREAM, MediaType.create( "text", "plain" ), MediaType.create( "application", "x-bzip2" ) );

    final XDataService xDataService;

    final SiteService siteService;

    final List<ContentProcessor> contentProcessors;

    final List<ContentValidator> contentValidators;

    protected final PageDescriptorService pageDescriptorService;

    protected final PartDescriptorService partDescriptorService;

    protected final LayoutDescriptorService layoutDescriptorService;

    final boolean allowUnsafeAttachmentNames;

    AbstractCreatingOrUpdatingContentCommand( final Builder<?> builder )
    {
        super( builder );
        this.xDataService = builder.xDataService;
        this.siteService = builder.siteService;
        this.contentProcessors = List.copyOf( builder.contentProcessors );
        this.contentValidators = List.copyOf( builder.contentValidators );
        this.allowUnsafeAttachmentNames = builder.allowUnsafeAttachmentNames;
        this.pageDescriptorService = builder.pageDescriptorService;
        this.partDescriptorService = builder.partDescriptorService;
        this.layoutDescriptorService = builder.layoutDescriptorService;
    }

    public static class Builder<B extends Builder<B>>
        extends AbstractContentCommand.Builder<B>
    {
        private XDataService xDataService;

        private SiteService siteService;

        private List<ContentProcessor> contentProcessors = List.of();

        private List<ContentValidator> contentValidators = List.of();

        private boolean allowUnsafeAttachmentNames;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        Builder()
        {
        }

        Builder( final AbstractCreatingOrUpdatingContentCommand source )
        {
            super( source );
            this.xDataService = source.xDataService;
            this.siteService = source.siteService;
            this.contentProcessors = source.contentProcessors;
            this.contentValidators = source.contentValidators;
            this.pageDescriptorService = source.pageDescriptorService;
            this.partDescriptorService = source.partDescriptorService;
            this.layoutDescriptorService = source.layoutDescriptorService;
        }

        @SuppressWarnings("unchecked")
        B xDataService( final XDataService xDataService )
        {
            this.xDataService = xDataService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B siteService( final SiteService siteService )
        {
            this.siteService = siteService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B contentProcessors( final List<ContentProcessor> contentProcessors )
        {
            this.contentProcessors = contentProcessors;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B contentValidators( final List<ContentValidator> contentValidators )
        {
            this.contentValidators = contentValidators;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B allowUnsafeAttachmentNames( final boolean allowUnsafeAttachmentNames )
        {
            this.allowUnsafeAttachmentNames = allowUnsafeAttachmentNames;
            return (B) this;
        }

        B pageDescriptorService( final PageDescriptorService pageDescriptorService )
        {
            this.pageDescriptorService = pageDescriptorService;
            return (B) this;
        }

        B partDescriptorService( final PartDescriptorService partDescriptorService )
        {
            this.partDescriptorService = partDescriptorService;
            return (B) this;
        }

        B layoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
        {
            this.layoutDescriptorService = layoutDescriptorService;
            return (B) this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( xDataService, "xDataService cannot be null" );
        }
    }

    static void populateByteSourceProperties( final ByteSource byteSource, Attachment.Builder builder )
    {
        try (InputStream inputStream = byteSource.openStream(); DigestInputStream digestInputStream = new DigestInputStream( inputStream,
                                                                                                                             MessageDigests.sha512() ))
        {
            long size = ByteStreams.exhaust( digestInputStream );
            builder.size( size );
            builder.sha512( HexCoder.toHex( digestInputStream.getMessageDigest().digest() ) );

        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    void validateCreateAttachments( final CreateAttachments createAttachments )
    {
        if ( !allowUnsafeAttachmentNames && createAttachments != null )
        {
            for ( CreateAttachment attachment : createAttachments )
            {
                if ( !FileNames.isSafeFileName( attachment.getName() ) )
                {
                    throw new IllegalArgumentException( "Unsafe attachment name " + attachment.getName() );
                }
            }
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
        return fileName.endsWith( ".exe" ) || fileName.endsWith( ".msi" ) || fileName.endsWith( ".dmg" ) || fileName.endsWith( ".bat" ) ||
            fileName.endsWith( ".sh" );
    }
}


