package com.enonic.xp.core.impl.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.DigestInputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.Mixin;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.core.internal.HexCoder;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigService;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.MixinMappingService;
import com.enonic.xp.site.MixinOption;
import com.enonic.xp.site.MixinOptions;

class AbstractCreatingOrUpdatingContentCommand
    extends AbstractContentCommand
{
    private static final ImmutableList<MediaType> BINARY_CONTENT_TYPES =
        ImmutableList.of( MediaType.OCTET_STREAM, MediaType.create( "application", "force-download" ),
                          MediaType.create( "application", "x-force-download" ) );

    private static final ImmutableList<MediaType> EXECUTABLE_CONTENT_TYPES =
        ImmutableList.of( MediaType.OCTET_STREAM, MediaType.create( "text", "plain" ), MediaType.create( "application", "x-bzip2" ) );

    final MixinService mixinService;

    final CmsService cmsService;

    final List<ContentProcessor> contentProcessors;

    final List<ContentValidator> contentValidators;

    protected final PageDescriptorService pageDescriptorService;

    protected final PartDescriptorService partDescriptorService;

    protected final LayoutDescriptorService layoutDescriptorService;

    final MixinMappingService mixinMappingService;

    final SiteConfigService siteConfigService;

    final boolean allowUnsafeAttachmentNames;

    AbstractCreatingOrUpdatingContentCommand( final Builder<?> builder )
    {
        super( builder );
        this.mixinService = builder.mixinService;
        this.cmsService = builder.cmsService;
        this.contentProcessors = List.copyOf( builder.contentProcessors );
        this.contentValidators = List.copyOf( builder.contentValidators );
        this.allowUnsafeAttachmentNames = builder.allowUnsafeAttachmentNames;
        this.pageDescriptorService = builder.pageDescriptorService;
        this.partDescriptorService = builder.partDescriptorService;
        this.layoutDescriptorService = builder.layoutDescriptorService;
        this.mixinMappingService = builder.mixinMappingService;
        this.siteConfigService = builder.siteConfigService;
    }

    Mixins mergeExtraData( final ContentTypeName type, final PropertyTree data, final ContentPath parent, final Mixins extraDatas )
    {
        final Mixins.Builder result = Mixins.create();
        final ApplicationKeys.Builder applicationKeys = ApplicationKeys.create().add( ApplicationKey.PORTAL );

        if ( type.isSite() )
        {
            SiteConfigsDataSerializer.fromData( data.getRoot() )
                .stream()
                .map( SiteConfig::getApplicationKey )
                .forEach( applicationKeys::add );
        }
        else
        {
            siteConfigService.getSiteConfigs( parent ).stream().map( SiteConfig::getApplicationKey ).forEach( applicationKeys::add );
        }

        final MixinOptions allowedMixins = mixinMappingService.getMixinMappingOptions( type, applicationKeys.build() );

        final Set<MixinName> allowedMixinName =
            allowedMixins.stream().map( MixinOption::mixinDescriptor ).map( MixinDescriptor::getName ).collect( Collectors.toSet() );

        for ( Mixin extraData : extraDatas )
        {
            if ( !allowedMixinName.contains( extraData.getName() ) )
            {
                throw new IllegalArgumentException( "Not allowed mixinName: " + extraData.getName() );
            }
        }

        for ( MixinOption mixinOption : allowedMixins )
        {
            final boolean isOptional = mixinOption.optional();
            final MixinDescriptor mixinDescriptor = mixinOption.mixinDescriptor();
            final Mixin mixin = extraDatas.getByName( mixinDescriptor.getName() );

            if ( mixin == null )
            {
                if ( !isOptional )
                {
                    result.add( new Mixin( mixinDescriptor.getName(), new PropertyTree() ) );
                }
            }
            else
            {
                result.add( mixin );
            }
        }

        return result.build();
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

    protected boolean isExecutableContentType( final String contentType, final ContentName fileName )
    {
        final MediaType mediaType = MediaType.parse( contentType );
        return EXECUTABLE_CONTENT_TYPES.stream().anyMatch( mediaType::is ) && isExecutableFileName( fileName.toString() );
    }

    void checkAdminAccess()
        throws ForbiddenAccessException
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authInfo = context.getAuthInfo();
        final ProjectName projectName = ProjectName.from( context.getRepositoryId() );

        if ( !ProjectAccessHelper.hasAccess( authInfo, projectName, ProjectRole.OWNER ) )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }

    private boolean isExecutableFileName( final String fileName )
    {
        return fileName.endsWith( ".exe" ) || fileName.endsWith( ".msi" ) || fileName.endsWith( ".dmg" ) || fileName.endsWith( ".bat" ) ||
            fileName.endsWith( ".sh" );
    }

    public static class Builder<B extends Builder<B>>
        extends AbstractContentCommand.Builder<B>
    {
        private MixinService mixinService;

        private CmsService cmsService;

        private List<ContentProcessor> contentProcessors = List.of();

        private List<ContentValidator> contentValidators = List.of();

        private boolean allowUnsafeAttachmentNames;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        protected MixinMappingService mixinMappingService;

        private SiteConfigService siteConfigService;

        Builder()
        {
        }

        Builder( final AbstractCreatingOrUpdatingContentCommand source )
        {
            super( source );
            this.mixinService = source.mixinService;
            this.cmsService = source.cmsService;
            this.contentProcessors = source.contentProcessors;
            this.contentValidators = source.contentValidators;
            this.pageDescriptorService = source.pageDescriptorService;
            this.partDescriptorService = source.partDescriptorService;
            this.layoutDescriptorService = source.layoutDescriptorService;
            this.mixinMappingService = source.mixinMappingService;
            this.siteConfigService = source.siteConfigService;
        }

        @SuppressWarnings("unchecked")
        B mixinService( final MixinService mixinService )
        {
            this.mixinService = mixinService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B cmsService( final CmsService siteService )
        {
            this.cmsService = siteService;
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

        B mixinMappingService( final MixinMappingService mixinMappingService )
        {
            this.mixinMappingService = mixinMappingService;
            return (B) this;
        }

        B siteConfigService( final SiteConfigService siteConfigService )
        {
            this.siteConfigService = siteConfigService;
            return (B) this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( mixinService );
        }
    }

}


