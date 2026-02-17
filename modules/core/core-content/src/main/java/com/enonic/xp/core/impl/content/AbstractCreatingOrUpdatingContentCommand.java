package com.enonic.xp.core.impl.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.DigestInputStream;
import java.util.EnumSet;
import java.util.HexFormat;
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
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.Mixin;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.content.PatchableContent;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.core.impl.content.validate.InputValidator;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypes;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.DescriptorBasedComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.Regions;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigService;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
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

    final boolean layersSync;

    AbstractCreatingOrUpdatingContentCommand( final Builder<?> builder )
    {
        super( builder );
        this.layersSync = builder.layersSync;
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

    Mixins mergeMixins( final ContentTypeName type, final PropertyTree data, final ContentPath parent, final Mixins mixins )
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

        for ( Mixin mixin : mixins )
        {
            if ( !allowedMixinName.contains( mixin.getName() ) )
            {
                throw new IllegalArgumentException( "Not allowed mixinName: " + mixin.getName() );
            }
        }

        for ( MixinOption mixinOption : allowedMixins )
        {
            final boolean isOptional = mixinOption.optional();
            final MixinDescriptor mixinDescriptor = mixinOption.mixinDescriptor();
            final Mixin mixin = mixins.getByName( mixinDescriptor.getName() );

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
            builder.sha512( HexFormat.of().formatHex( digestInputStream.getMessageDigest().digest() ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    void validateContentData( ContentTypeName contentTypeName, final PropertyTree data )
    {
        if ( contentTypeName.isUnstructured() )
        {
            return;
        }

        ContentType contentType = contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) );
        if ( contentType != null )
        {
            validateForm( contentType.getForm(), data, "Incorrect content property" );
        }
    }

    void validateMixins( final Mixins mixins )
    {
        for ( Mixin mixin : mixins )
        {
            MixinDescriptor mixinDescriptor = mixinService.getByName( mixin.getName() );
            if ( mixinDescriptor != null )
            {
                validateForm( mixinDescriptor.getForm(), mixin.getData(), "Incorrect mixin property" );
            }
        }
    }

    void validateSiteConfigs( final PropertyTree siteConfigsData )
    {
        SiteConfigs siteConfigs = SiteConfigsDataSerializer.fromData( siteConfigsData.getRoot() );

        for ( SiteConfig siteConfig : siteConfigs )
        {
            CmsDescriptor descriptor = cmsService.getDescriptor( siteConfig.getApplicationKey() );
            if ( descriptor != null )
            {
                validateForm( descriptor.getForm(), siteConfig.getConfig(), "Incorrect site config property" );
            }
        }
    }

    void validatePage( final Page page )
    {
        if ( page == null )
        {
            return;
        }

        PageDescriptor descriptor = getPageDescriptor( page );
        if ( descriptor != null )
        {
            validateForm( descriptor.getConfig(), page.getConfig(), "Incorrect page property" );
        }

        if ( page.hasRegions() )
        {
            validateRegions( page.getRegions() );
        }
    }

    private void validateRegions( final Regions regions )
    {
        for ( Region region : regions )
        {
            for ( Component component : region.getComponents() )
            {
                if ( !( component instanceof DescriptorBasedComponent descriptorBasedComponent ) ||
                    !descriptorBasedComponent.hasDescriptor() )
                {
                    continue;
                }

                if ( descriptorBasedComponent instanceof PartComponent part )
                {
                    PartDescriptor d = partDescriptorService.getByKey( part.getDescriptor() );
                    if ( d != null )
                    {
                        validateForm( d.getConfig(), part.getConfig(), "Incorrect part component property" );
                    }
                }
                else if ( descriptorBasedComponent instanceof LayoutComponent layout )
                {
                    LayoutDescriptor d = layoutDescriptorService.getByKey( layout.getDescriptor() );
                    if ( d != null )
                    {
                        validateForm( d.getConfig(), layout.getConfig(), "Incorrect layout component property" );
                        if ( layout.hasRegions() )
                        {
                            validateRegions( layout.getRegions() );
                        }
                    }
                }
            }
        }
    }

    private void validateForm( Form form, PropertyTree data, String errorMessage )
    {
        if ( form == null || data == null )
        {
            return;
        }
        try
        {
            InputValidator.create().form( form ).inputTypeResolver( InputTypes.BUILTIN ).build().validate( data );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( errorMessage, e );
        }
    }

    private PageDescriptor getPageDescriptor( final Page page )
    {
        if ( page.hasDescriptor() )
        {
            return pageDescriptorService.getByKey( page.getDescriptor() );
        }

        if ( page.hasTemplate() )
        {
            final PageTemplate pageTemplate = (PageTemplate) getContent( page.getTemplate().getContentId() );
            if ( pageTemplate != null && pageTemplate.getPage() != null && pageTemplate.getPage().hasDescriptor() )
            {
                return pageDescriptorService.getByKey( pageTemplate.getPage().getDescriptor() );
            }
        }

        return null;
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

    void checkOwnerAccess()
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

    protected boolean isContentTheSame( Content c1, Content c2 )
    {
        return Objects.equals( c1.getId(), c2.getId() ) && Objects.equals( c1.getPath(), c2.getPath() ) &&
            Objects.equals( c1.getDisplayName(), c2.getDisplayName() ) && Objects.equals( c1.getType(), c2.getType() ) &&
            Objects.equals( c1.getCreator(), c2.getCreator() ) && Objects.equals( c1.getOwner(), c2.getOwner() ) &&
            Objects.equals( c1.getCreatedTime(), c2.getCreatedTime() ) && Objects.equals( c1.getInherit(), c2.getInherit() ) &&
            Objects.equals( c1.getOriginProject(), c2.getOriginProject() ) && Objects.equals( c1.getChildOrder(), c2.getChildOrder() ) &&
            Objects.equals( c1.getPermissions(), c2.getPermissions() ) && Objects.equals( c1.getAttachments(), c2.getAttachments() ) &&
            Objects.equals( c1.getData(), c2.getData() ) && Objects.equals( c1.getMixins(), c2.getMixins() ) &&
            Objects.equals( c1.getPage(), c2.getPage() ) && Objects.equals( c1.getLanguage(), c2.getLanguage() ) &&
            Objects.equals( c1.getPublishInfo(), c2.getPublishInfo() ) && Objects.equals( c1.getWorkflowInfo(), c2.getWorkflowInfo() ) &&
            Objects.equals( c1.getManualOrderValue(), c2.getManualOrderValue() ) &&
            Objects.equals( c1.getOriginalName(), c2.getOriginalName() ) &&
            Objects.equals( c1.getOriginalParentPath(), c2.getOriginalParentPath() ) &&
            Objects.equals( c1.getArchivedTime(), c2.getArchivedTime() ) && Objects.equals( c1.getArchivedBy(), c2.getArchivedBy() ) &&
            Objects.equals( c1.getVariantOf(), c2.getVariantOf() );
    }

    protected static Content afterUpdate( final Content editedContent )
    {
        final PatchableContent patchableContent = new PatchableContent( editedContent );
        patchableContent.publishInfo.setPatcher( c -> c.publishInfo.originalValue == null
            ? null
            : ContentPublishInfo.create( editedContent.getPublishInfo() ).time( null ).build() );
        patchableContent.inherit.setPatcher( c -> stopDataInherit( c.inherit.originalValue ) );

        return patchableContent.build();
    }

    protected static Set<ContentInheritType> stopDataInherit( final Set<ContentInheritType> currentInherit )
    {
        if ( currentInherit == null || currentInherit.isEmpty() )
        {
            return currentInherit;
        }

        final EnumSet<ContentInheritType> newInherit = EnumSet.copyOf( currentInherit );
        newInherit.remove( ContentInheritType.CONTENT );
        newInherit.remove( ContentInheritType.NAME );
        return newInherit;
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

        private MixinMappingService mixinMappingService;

        private SiteConfigService siteConfigService;

        private boolean layersSync;

        Builder()
        {
        }

        B layersSync()
        {
            this.layersSync = true;
            return (B) this;
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
        }
    }

}


