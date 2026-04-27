package com.enonic.xp.core.impl.content;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.schema.content.ContentTypeFromMimeTypeResolver;
import com.enonic.xp.schema.content.ContentTypeName;

import static java.util.Objects.requireNonNull;

final class CreateMediaCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final CreateMediaParams params;

    private final MediaInfoService mediaInfoService;

    private CreateMediaCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mediaInfoService = builder.mediaInfoService;
    }

    Content execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        final MediaInfo mediaInfo = mediaInfoService.parseMediaInfo( params.getByteSource() );

        final String mimeType = requireNonNull( mediaInfo.getMediaType(), "Unable to resolve media type" );

        final ContentTypeName resolvedTypeFromMimeType = ContentTypeFromMimeTypeResolver.resolve( mimeType );
        final ContentTypeName type = resolvedTypeFromMimeType != null
            ? resolvedTypeFromMimeType
            : guessExecutableByFilename( mimeType, params.getName().toString() );

        final PropertyTree data = new PropertyTree();
        new MediaFormDataBuilder().type( type )
            .attachment( params.getName().toString() )
            .focalPoint( params.getFocalPoint() )
            .caption( params.getCaption() )
            .altText( params.getAltText() )
            .artist( params.getArtistList() )
            .copyright( params.getCopyright() )
            .tags( params.getTagList() )
            .build( data );

        final CreateAttachment mediaAttachment = CreateAttachment.create()
            .name( params.getName().toString() )
            .mimeType( mimeType )
            .label( "source" )
            .byteSource( params.getByteSource() )
            .text( type.isTextualMedia() ? mediaInfo.getTextContent() : null )
            .build();

        final CreateContentParams createContentParams = CreateContentParams.create()
            .name( params.getName() )
            .parent( params.getParent() )
            .requireValid( true )
            .type( type )
            .displayName( trimExtension( params.getName() ) )
            .contentData( data )
            .createAttachments( CreateAttachments.from( mediaAttachment ) )
            .build();

        final CreateContentCommand createCommand = CreateContentCommand.create()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .cmsService( this.cmsService )
            .mixinService( this.mixinService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .mixinMappingService( this.mixinMappingService )
            .siteConfigService( this.siteConfigService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .allowUnsafeAttachmentNames( this.allowUnsafeAttachmentNames )
            .mediaInfo( mediaInfo )
            .params( createContentParams )
            .build();

        return createCommand.execute();
    }

    private String trimExtension( final ContentName name )
    {
        final String nameAsString = name.toString();
        final int endIndex = nameAsString.lastIndexOf( '.' );
        return ( endIndex < 0 ) ? nameAsString : nameAsString.substring( 0, endIndex );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private CreateMediaParams params;

        private MediaInfoService mediaInfoService;

        public Builder params( final CreateMediaParams params )
        {
            this.params = params;
            return this;
        }

        public Builder mediaInfoService( final MediaInfoService value )
        {
            this.mediaInfoService = value;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( params, "params cannot be null" );
        }

        public CreateMediaCommand build()
        {
            validate();
            return new CreateMediaCommand( this );
        }
    }

}
