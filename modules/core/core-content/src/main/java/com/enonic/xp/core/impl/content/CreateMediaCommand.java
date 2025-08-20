package com.enonic.xp.core.impl.content;

import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.schema.content.ContentTypeFromMimeTypeResolver;
import com.enonic.xp.schema.content.ContentTypeName;

final class CreateMediaCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final CreateMediaParams params;

    private final MediaInfoService mediaInfoService;

    private final FormDefaultValuesProcessor formDefaultValuesProcessor;

    private CreateMediaCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mediaInfoService = builder.mediaInfoService;
        this.formDefaultValuesProcessor = builder.formDefaultValuesProcessor;
    }

    Content execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        final MediaInfo mediaInfo = mediaInfoService.parseMediaInfo( params.getByteSource() );

        if ( ( params.getMimeType() == null || isBinaryContentType( params.getMimeType() ) ) && mediaInfo.getMediaType() != null )
        {
            params.mimeType( mediaInfo.getMediaType() );
        }

        Preconditions.checkNotNull( params.getMimeType(), "Unable to resolve media type" );

        final ContentTypeName resolvedTypeFromMimeType = ContentTypeFromMimeTypeResolver.resolve( params.getMimeType() );
        final ContentTypeName type = resolvedTypeFromMimeType != null
            ? resolvedTypeFromMimeType
            : isExecutableContentType( params.getMimeType(), params.getName() )
                ? ContentTypeName.executableMedia()
                : ContentTypeName.unknownMedia();

        final PropertyTree data = new PropertyTree();
        new MediaFormDataBuilder().
            type( type ).
            attachment( params.getName().toString() ).
            focalX( params.getFocalX() ).
            focalY( params.getFocalY() ).
            caption( params.getCaption() ).
            altText( params.getAltText() ).
            artist( params.getArtist() != null ? List.of( params.getArtist() ) : List.of() ).
            copyright( params.getCopyright() ).
            tags( params.getTags() != null ? List.of( params.getTags() ) : List.of() ).
            build( data );

        final CreateAttachment mediaAttachment = CreateAttachment.create().
            name( params.getName().toString() ).
            mimeType( params.getMimeType() ).
            label( "source" ).
            byteSource( params.getByteSource() ).
            text( type.isTextualMedia() ? mediaInfo.getTextContent() : null ).
            build();

        final CreateContentParams createContentParams = CreateContentParams.create().
            name( params.getName() ).
            parent( params.getParent() ).
            requireValid( true ).
            type( type ).
            displayName( trimExtension( params.getName() ) ).
            contentData( data ).
            createAttachments( CreateAttachments.from( mediaAttachment ) ).
            build();

        final CreateContentCommand createCommand = CreateContentCommand.create( this ).
            mediaInfo( mediaInfo ).
            translator( this.translator ).
            params( createContentParams ).
            siteService( this.siteService ).
            xDataService( this.xDataService ).
            formDefaultValuesProcessor( this.formDefaultValuesProcessor ).
            pageDescriptorService( this.pageDescriptorService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            allowUnsafeAttachmentNames( this.allowUnsafeAttachmentNames ).
            build();

        return createCommand.execute();
    }

    private String trimExtension( final ContentName name )
    {
        if ( name.toString().lastIndexOf( "." ) < 0 )
        {
            return name.toString();
        }
        return name.toString().substring( 0, name.toString().lastIndexOf( "." ) );
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

        private FormDefaultValuesProcessor formDefaultValuesProcessor;

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

        public Builder formDefaultValuesProcessor( final FormDefaultValuesProcessor formDefaultValuesProcessor )
        {
            this.formDefaultValuesProcessor = formDefaultValuesProcessor;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params, "params must be given" );
            Preconditions.checkNotNull( formDefaultValuesProcessor );
        }

        public CreateMediaCommand build()
        {
            validate();
            return new CreateMediaCommand( this );
        }
    }

}
